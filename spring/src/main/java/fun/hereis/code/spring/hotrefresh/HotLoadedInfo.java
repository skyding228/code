package fun.hereis.code.spring.hotrefresh;

/**
 * 加载后的信息
 * @author weichunhe
 * created at 2021/1/26
 */
public class HotLoadedInfo {
    /**
     * 方法拦截类
     */
    private ProxyMethodInterceptor proxyMethodInterceptor;

    /**
     * 代理类
     */
    private Object proxy;

    /**
     * getter
     * @return 方法拦截类
     */
    public ProxyMethodInterceptor getProxyMethodInterceptor() {
        return proxyMethodInterceptor;
    }

    public void setProxyMethodInterceptor(ProxyMethodInterceptor proxyMethodInterceptor) {
        this.proxyMethodInterceptor = proxyMethodInterceptor;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }
}
