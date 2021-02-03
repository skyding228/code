package fun.hereis.code.spring.hotrefresh.resource;

import fun.hereis.code.spring.Lettuce;
import fun.hereis.code.utils.cache.GuavaCache;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author weichunhe
 * created at 2021/2/1
 */
public class DynamicResourceTransformer implements ResourceTransformer {

    private static Logger logger = LoggerFactory.getLogger(DynamicResourceTransformer.class);
    private final String CACHE_KEY = "activity:dynamic:resource:hash";
    private StatefulRedisClusterConnection<String, String> clusterConnection;
    private GuavaCache<Map<String, String>> resourceCache = null;

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
        clusterConnection = Lettuce.connect(redisNodes, redisPassword);
        resourceCache = GuavaCache.asyncGuavaCache(5, () -> {
            Map<String, String> cache = clusterConnection.sync().hgetall(CACHE_KEY);
            if (MapUtils.isEmpty(cache)) {
                cache = MapUtils.EMPTY_MAP;
            }
            logger.info("DynamicResourceTransformer cached resources : {}", cache.keySet());
            return cache;
        });
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
        //无需转换
        String value = resourceCache.get().get(path);
        if (value == null) {
            return resource;
        }
        logger.info("DynamicResourceTransformer transformed " + path);
        return new ByteArrayResource(value.getBytes());
    }
}
