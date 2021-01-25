package fun.hereis.code.spring.hotrefresh;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author weichunhe
 * created at 2021/1/25
 */
public class HotClassLoader extends ClassLoader {

    private static HotClassLoader hotClassLoader =new HotClassLoader();

    private static String baseUrl = "http://a.hereis.fun/alijs/";
    private static final String suffix = ".class";
    private static final ClassLoader parent = HotClassLoader.class.getClassLoader();


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
            bean = hotClassLoader.loadClass(tClass.getName()+suffix).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新注册bean
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(bean);
        autowireCapableBeanFactory.initializeBean(bean,beanName);
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory ) applicationContext.getBeanFactory();
        if(applicationContext.containsBean(beanName)){
            defaultListableBeanFactory.destroySingleton(beanName);
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(tClass);
        final Object object = bean;
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Method proxyMethod = object.getClass().getMethod(method.getName(),method.getParameterTypes());
                return proxyMethod.invoke(object,objects);
            }
        });
        T newBean = (T) enhancer.create();
        defaultListableBeanFactory.registerSingleton(beanName,newBean);
        return newBean;
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
}
