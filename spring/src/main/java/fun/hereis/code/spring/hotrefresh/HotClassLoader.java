package fun.hereis.code.spring.hotrefresh;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weichunhe
 * created at 2021/1/25
 */
public class HotClassLoader extends ClassLoader {


    private static String baseUrl = "http://a.hereis.fun/alijs/";
    private static final String suffix = ".class";
    private static final ClassLoader parent = HotClassLoader.class.getClassLoader();

    private static Map<String/*beanName*/,HotLoadedInfo> loadedBeanMap = new HashMap<>();


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (!name.endsWith(suffix)) {
            return parent.loadClass(name);
        }

        try {
            String path = baseUrl + name;
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            //设置超时
            httpURLConnection.setConnectTimeout(1000*15);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();

            byte[] bytes = StreamUtils.copyToByteArray(httpURLConnection.getInputStream());
            return defineClass(name.replace(suffix,""), bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 重新加载bean
     * @param tClass 需要重新加载的类型
     * @param applicationContext spring 容器
     * @param beanName 需要重新加载的bean名称
     * @param <T> 重新加载类型
     * @return cglib代理后的类
     */
    public static <T> T reload(Class<T> tClass, ConfigurableApplicationContext applicationContext, String beanName){
        Object bean = null;
        try {
            HotClassLoader hotClassLoader =new HotClassLoader();
            bean = hotClassLoader.loadClass(tClass.getName()+suffix).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新注册bean
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(bean);
        autowireCapableBeanFactory.initializeBean(bean,beanName);
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory ) applicationContext.getBeanFactory();
        final Object object = bean;

        HotLoadedInfo loadedInfo = loadedBeanMap.get(beanName);
        if (loadedInfo == null){
            loadedInfo = new HotLoadedInfo();
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(tClass);
            ProxyMethodInterceptor proxyMethodInterceptor = new ProxyMethodInterceptor();
            proxyMethodInterceptor.setDelegate(object);
            loadedInfo.setProxyMethodInterceptor(proxyMethodInterceptor);
            enhancer.setCallback(proxyMethodInterceptor);
            T newBean = (T) enhancer.create();
            loadedInfo.setProxy(newBean);
            if(applicationContext.containsBean(beanName)){
                String[] dependents = defaultListableBeanFactory.getDependentBeans(beanName);
                //没有依赖它的bean，就重新注册一下
                if(dependents.length == 0){
                    defaultListableBeanFactory.destroySingleton(beanName);
                    defaultListableBeanFactory.registerSingleton(beanName,newBean);
                }else {
                    for (String dependent : dependents) {
                        Object dependentBean = applicationContext.getBean(dependent);
                        ManualInject.buildAutowiringMetadata(dependentBean.getClass()).wire(dependentBean,newBean);
                    }
                    Map<String, Object> singletons = (Map<String, Object>) defaultListableBeanFactory.getSingletonMutex();
                    if(singletons.containsKey(beanName)){
                        singletons.put(beanName,newBean);
                    }
                }

                String[] dependencies = defaultListableBeanFactory.getDependenciesForBean(beanName);
                for (String dependency : dependencies) {
                    if(loadedBeanMap.containsKey(dependency)){
                        ManualInject.buildAutowiringMetadata(bean.getClass()).wire(bean,loadedBeanMap.get(dependency).getProxy());
                    }
                }
            }else {
                defaultListableBeanFactory.registerSingleton(beanName,newBean);
            }

            loadedBeanMap.put(beanName,loadedInfo);
        }else {
            loadedInfo.getProxyMethodInterceptor().setDelegate(object);
        }

        execAfterHotLoad(bean);
        System.out.println(tClass.getName()+" hotreload.");
        return (T) loadedInfo.getProxy();
    }

    /**
     * 设置查找类的基础地址
     * @param baseUrl 基础地址
     */
    public static void setBaseUrl(String baseUrl){
        if(!baseUrl.endsWith("/")){
            baseUrl += "/";
        }
        HotClassLoader.baseUrl = baseUrl;
    }

    /**
     * 执行加载后的方法
     * @param bean
     */
    private static void execAfterHotLoad(Object bean){
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if(AnnotatedElementUtils.hasAnnotation(method,AfterHotLoad.class)){
                try {
                    method.invoke(bean);
                    System.out.println(method.getName()+" 执行成功!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
