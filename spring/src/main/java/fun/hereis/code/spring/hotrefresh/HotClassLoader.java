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
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            //设置超时
            httpURLConnection.setConnectTimeout(1000*15);
            //设置请求方式，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL引用的资源的通信链接（如果尚未建立这样的连接）。
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
     * @param tClass 需要重新加载的类
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

        execAfterHotLoad(bean);
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
                for (String dependent : dependents) {
                    Object dependentBean = applicationContext.getBean(dependent);
                    ManualInject.buildAutowiringMetadata(dependentBean.getClass()).wire(dependentBean,newBean);
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
        T loaded = (T) applicationContext.getBean(beanName);
        System.out.println(tClass.getName()+" hotreload.");

        return loaded;
    }

    /**
     * 设置查找类的基础地址
     * @param baseUrl
     */
    public static void setBaseUrl(String baseUrl){
        if(!baseUrl.endsWith("/")){
            baseUrl += "/";
        }
        HotClassLoader.baseUrl = baseUrl;
    }

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
