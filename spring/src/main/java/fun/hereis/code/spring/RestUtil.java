package fun.hereis.code.spring;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * restful 请求工具类
 * @author weichunhe
 * created at 2019-10-28
 */
public class RestUtil {

    private static Logger log = LoggerFactory.getLogger(RestUtil.class);

    private static HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

    private static final String FORM_UTF_8 = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8";

    private static RestTemplate rest = null;

    static {
        rest = new RestTemplate(getClientHttpRequestFactory());
        rest.setErrorHandler(new RestErrorHandler());
    }

    /**
     * 发送post 请求
     *
     * @param rest rest object or null
     * @param url url
     * @param param object
     * @param responseType response type
     * @param <T> generics type
     * @return response object
     */
    public static <T> T postJSON(RestTemplate rest, String url, Object param, Class<T> responseType) {
        if(rest == null){
            rest = RestUtil.rest;
        }
        HttpEntity<String> formEntity = makePostJSONEntity(param);
        T result = rest.postForObject(url, formEntity, responseType);
        log.debug("rest-post-json 响应信息:{}", JsonUtil.toJson(result));
        return result;
    }

    /**
     * 发送post 表单请求
     *
     * @param url url
     * @param paramMap map
     * @param responseType responseType
     * @param <T> generics type
     * @return response object
     */
    public static <T> T postForm(String url, Map<String, ? extends Object> paramMap, Class<T> responseType) {
        HttpEntity<String> formEntity = makeFormEntity(paramMap);
        log.debug("rest-post-form send {} to {}.", url, formEntity);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = rest.postForEntity(url, formEntity, String.class);
        } catch (Exception e) {
            log.error("An exception occurs while posting {} to {}", formEntity, url, e);
            return null;
        }
        log.debug("rest-post-form 响应信息:{}", responseEntity.getBody());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            if(String.class.equals(responseType)){
                return (T) responseEntity.getBody();
            }else {
                return JsonUtil.fromJson(responseEntity.getBody(), responseType);
            }
        }
        return null;
    }

    /**
     * 生成json形式的请求头
     *
     * @param param object
     * @return httpEntity
     */
    public static HttpEntity<String> makePostJSONEntity(Object param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> formEntity = new HttpEntity<String>(
                JsonUtil.toJson(param), headers);
        log.debug("rest-post-json-请求参数:{}", formEntity.toString());
        return formEntity;
    }

    /**
     * make a httpEntity with a map
     * @param param map
     * @return httpEntity
     */
    public static HttpEntity<String> makeFormEntity(Map<String, ? extends Object> param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", FORM_UTF_8);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> formEntity = new HttpEntity<String>(
                makeGetParamContent(param), headers);
        log.debug("rest-post-text-请求参数:{}", formEntity.toString());
        return formEntity;
    }


    /**
     * 生成Get请求内容
     *
     * @param param map
     * @param excludes the keys need to be excluded
     * @return url param
     */
    public static String makeGetParamContent(Map<String, ? extends Object> param, String... excludes) {
        StringBuilder content = new StringBuilder();
        List<String> excludeKeys = Arrays.asList(excludes);
        param.forEach((key, v) -> {
            content.append(key).append("=").append(v).append("&");
        });
        if (content.length() > 0) {
            content.deleteCharAt(content.length() - 1);
        }
        return content.toString();
    }

    private static HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
        if (clientHttpRequestFactory != null) {
            return clientHttpRequestFactory;
        }
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, null, null,
                new NoopHostnameVerifier());

        /* Custom DNS resolver */
        DnsResolver dnsResolver = new SystemDefaultDnsResolver() {
            @Override
            public InetAddress[] resolve(final String host) throws UnknownHostException {
//                InetAddress[] addresses = HttpClientUtils.ns.lookupAllHostAddr(host);
//                if (addresses != null) {
//                    return addresses;
//                } else {
                return super.resolve(host);
//                }
            }
        };

        HttpClient httpClient = HttpClientBuilder.create().setSSLSocketFactory(csf)
                .setDnsResolver(dnsResolver)
                .build();
        clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        clientHttpRequestFactory.setConnectTimeout(30 * 1000);
        clientHttpRequestFactory.setReadTimeout(30 * 1000);

        return clientHttpRequestFactory;
    }

    public static class RestErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            log.error("An error occurs while rest {}", response);
        }
    }
}