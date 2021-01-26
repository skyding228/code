package fun.hereis.code.spring.hotrefresh;

/**
 * @author weichunhe
 * created at 2021/1/26
 */
public class HotLoadedInfo {

    private ProxyMethodInterceptor proxyMethodInterceptor;


    public ProxyMethodInterceptor getProxyMethodInterceptor() {
        return proxyMethodInterceptor;
    }

    public void setProxyMethodInterceptor(ProxyMethodInterceptor proxyMethodInterceptor) {
        this.proxyMethodInterceptor = proxyMethodInterceptor;
    }
}
