package fun.hereis.code.spring;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author weichunhe
 * created at 2020/1/20
 * http 客户端工具类
 */
public class OkHttp {
    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");

    private OkHttpClient okHttpClient;

    private OkHttp(OkHttpClient okHttp) {
        this.okHttpClient = okHttp;
    }

    /**
     * 创建一个实例对象
     *
     * @return okHttp
     */
    public static OkHttp createInstance() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS);
        return new OkHttp(builder.build());
    }

    /**
     * 使用代理创建一个实例对象
     * proxy=localhost:3821
     *
     * @return okHttp
     */
    public static OkHttp createInstanceWithProxy() {
        String proxyConfig = System.getProperty("proxy");
        if (StringUtils.isEmpty(proxyConfig)) {
            return createInstance();
        }
        String[] items = proxyConfig.split(":");
        SocketAddress socketAddress = new InetSocketAddress(items[0], Integer.valueOf(items[1]));
        Proxy proxy = new Proxy(Proxy.Type.HTTP, socketAddress);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxy)
                .connectTimeout(3, TimeUnit.SECONDS);
        return new OkHttp(builder.build());
    }

    /**
     * 同步get
     *
     * @param url 地址
     * @return 响应结果
     * @throws IOException 异常
     */
    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        Response response = execute(request);
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 同步post json数据
     *
     * @param url   地址
     * @param param 参数
     * @param returnType 响应结果类型
     * @param <R>
     * @return 结果
     */
    public <R> R post(String url, Object param, Class<R> returnType) {
        String json = JsonUtil.toJson(param);
        ;
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).post(body).build();

        try {
            Response response = execute(request);
            if (response.isSuccessful()) {
                String result = response.body().string();
                System.out.println(result);
                if (String.class.equals(returnType)) {
                    return (R) result;
                }
                return JsonUtil.fromJson(result, returnType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步post json数据
     *
     * @param url   地址
     * @param param 参数
     * @param callback 回调函数
     */
    public void asyncPost(String url, Object param, Callback callback) {
        String json = JsonUtil.toJson(param);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        enqueue(request, callback);
    }


    private Response execute(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request 请求体
     * @param callback 回调函数
     */
    private void enqueue(Request request, Callback callback) {
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 异步默认回调方法
     */
    public static Callback defaultCallBack = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println(call.request().toString());
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            System.out.println(call.request().toString());
            System.out.println(response.body().string());
        }
    };
}
