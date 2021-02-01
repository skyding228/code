package fun.hereis.code.spring.hotrefresh;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法拦截类
 * @author weichunhe
 * created at 2021/1/26
 */
public class ProxyMethodInterceptor implements MethodInterceptor {

    private Object delegate;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Method proxyMethod = delegate.getClass().getMethod(method.getName(),method.getParameterTypes());
        return proxyMethod.invoke(delegate,objects);
    }

    public void setDelegate(Object delegate) {
        this.delegate = delegate;
    }

}
