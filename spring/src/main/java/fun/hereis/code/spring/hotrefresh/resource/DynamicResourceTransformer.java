package fun.hereis.code.spring.hotrefresh.resource;

import com.google.common.collect.Lists;
import fun.hereis.code.spring.Redis;
import fun.hereis.code.utils.CollectionUtil;
import fun.hereis.code.utils.cache.GuavaCache;
import io.lettuce.core.internal.HostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author weichunhe
 * created at 2021/2/1
 */
public class DynamicResourceTransformer implements ResourceTransformer {

    private static Logger logger = LoggerFactory.getLogger(DynamicResourceTransformer.class);
    private final String CACHE_KEY = "activity:dynamic:resource:hash";
    private Redis redis;
    private GuavaCache<Map<String, FileSystemResource>> resourceCache = null;

    @Value("${server.context-path:/}")
    private String contextPath;
    /**
     * 明文内容的文件后缀
     */
    private List<String> textSuffixes = Lists.newArrayList(".html", ".htm", ".js", ".css");

    @Autowired
    public void init(@Qualifier("resourceHandlerMapping") HandlerMapping resourceHandlerMapping,
                     @Value("${dynamicResource.redis.nodes:ha-qwhd3-1.ha-qwhd3.zjjpt-redis.svc.huaarmcore.hpc:20054,ha-qwhd3-2.ha-qwhd3.zjjpt-redis.svc.huaarmcore.hpc:20054,ha-qwhd3-3.ha-qwhd3.zjjpt-redis.svc.huaarmcore.hpc:20054,ha-qwhd3-0.ha-qwhd3.zjjpt-redis.svc.huaarmcore.hpc:20054,ha-qwhd3-4.ha-qwhd3.zjjpt-redis.svc.huaarmcore.hpc:20054,ha-qwhd3-5.ha-qwhd3.zjjpt-redis.svc.huaarmcore.hpc:20054}") String redisNodes,
                     @Value("${dynamicResource.redis.password:qwhd3008}") String redisPassword) {
        SimpleUrlHandlerMapping handlerMapping = (SimpleUrlHandlerMapping) resourceHandlerMapping;
        logger.info("DynamicResourceTransformer init " + handlerMapping.getUrlMap().keySet());
        handlerMapping.getUrlMap().forEach((k, v) -> {
            ResourceHttpRequestHandler requestHandler = (ResourceHttpRequestHandler) v;
            requestHandler.getResourceTransformers().add(this);
        });

        logger.info("DynamicResourceTransformer init redis with {},{}", redisNodes, redisPassword);
        if (isReachable(redisNodes)) {
            redis = Redis.newInstance(redisNodes, redisPassword);
            resourceCache = GuavaCache.asyncGuavaCache(5, () -> {
                List<String> fileNames = redis.sync().hkeys(CACHE_KEY);
                logger.info("DynamicResourceTransformer cached resources : {}", fileNames);
                return cacheResource(fileNames);
            });
        }
    }

    private Map<String, FileSystemResource> cacheResource(List<String> names) {
        Map<String, FileSystemResource> resourceMap = new HashMap<>();
        if (CollectionUtil.isEmpty(names)) {
            return resourceMap;
        }
        for (String name : names) {
            //只加载本应用内的
            if (!name.startsWith(contextPath)) {
                continue;
            }
            String content = redis.sync().hget(CACHE_KEY, name);
            try {
                File file = new File(name.replaceAll("[/\\\\]", "_"));
                if (!file.exists()) {
                    file.createNewFile();
                }
                //判断是否是明文文件,否则要进行base64解码
                Optional<String> textSuffix = textSuffixes.stream().filter(name::endsWith).findFirst();
                byte[] bytes = textSuffix.isPresent() ? content.getBytes() : Base64Utils.decodeFromString(content);
                FileCopyUtils.copy(bytes, file);
                resourceMap.put(name, new FileSystemResource(file));
            } catch (IOException e) {
                logger.error("DynamicResourceTransformer load cache error. {}", name, e);
            }
        }
        return resourceMap;
    }

    private boolean isReachable(String nodes) {
        String[] addresses = nodes.replaceAll(" +", "").split(",");
        HostAndPort hostAndPort = HostAndPort.parse(addresses[0]);
        try {
            new Socket(hostAndPort.getHostText(), hostAndPort.getPort());
        } catch (IOException e) {
            logger.warn("DynamicResourceTransformer cluster is unreachable {}", nodes);
            return false;
        }
        return true;
    }

    /**
     * Transform the given resource.
     *
     * @param request          the current request
     * @param resource         the resource to transform
     * @param transformerChain the chain of remaining transformers to delegate to
     * @return the transformed resource (never {@code null})
     * @throws IOException if the transformation fails
     */
    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        String path = request.getRequestURI();
        if (resourceCache == null) {
            return resource;
        }
        //无需转换
        FileSystemResource value = resourceCache.get().get(path);
        if (value == null) {
            return resource;
        }
        logger.debug("DynamicResourceTransformer transformed " + path);
        return value;
    }
}
