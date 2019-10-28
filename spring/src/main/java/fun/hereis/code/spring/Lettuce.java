package fun.hereis.code.spring;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.internal.HostAndPort;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author weichunhe
 * created at 19-8-17
 */
public class Lettuce {

    private static boolean isCluster = false;

    private static StatefulRedisClusterConnection<String, String> clusterConnection;

    private static StatefulRedisConnection<String, String> connection;

    private static Properties redisProps = ClasspathPropertyUtil.load("redis.properties");

    private static Set<HostAndPort> nodes = getNodes();

    static {
        String password = (String) redisProps.get("redis.password");
        if (nodes.size() == 1) {
            //get the only one host and port
            HostAndPort hostAndPort = nodes.toArray(new HostAndPort[1])[0];
            RedisURI uri = RedisURI.create(hostAndPort.getHostText(), hostAndPort.getPort());
            if (StringUtils.isNotEmpty(password)) {
                uri.setPassword(password);
            }
            RedisClient client = RedisClient.create(uri);
            connection = client.connect();
        } else {
            isCluster = true;
            List<RedisURI> uris = new ArrayList<>();
            nodes.forEach(n -> {
                RedisURI uri = RedisURI.create(n.getHostText(), n.getPort());
                if (StringUtils.isNotEmpty(password)) {
                    uri.setPassword(password);
                }
                uris.add(uri);
            });
            RedisClusterClient clusterClient = RedisClusterClient.create(uris);
            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                    .enablePeriodicRefresh(true)
                    .enableAllAdaptiveRefreshTriggers()
                    .build();

            clusterClient.setOptions(ClusterClientOptions.builder()
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .build());
            clusterConnection = clusterClient.connect();
        }
    }

    private Lettuce() {
    }

    /**
     * @return 同步命令
     */
    public static RedisClusterCommands<String, String> sync() {
        return isCluster ? clusterConnection.sync() : connection.sync();
    }

    /**
     * @return 异步命令
     */
    public static RedisClusterAsyncCommands<String, String> async() {
        return isCluster ? clusterConnection.async() : connection.async();
    }

    /**
     * 获取配置节点
     *
     * @return
     */
    private static Set<HostAndPort> getNodes() {
        String config = redisProps.getProperty("redis.nodes", "localhost:6379");
        String[] addresses = config.replaceAll(" +", "").split(",");
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (String address : addresses) {
            if (StringUtils.isNotEmpty(address)) {
                hostAndPorts.add(HostAndPort.parse(address));
            }
        }
        return hostAndPorts;
    }

    /**
     * 同步请求获取整形类型
     * @param key key
     * @return null 表示不存在，或其他值
     */
    public static Long getLong(String key){
        String val = sync().get(key);
        if (org.springframework.util.StringUtils.isEmpty(val)) {
            return null;
        }
        return Long.valueOf(val);
    }

    /**
     * demo
     * @param args args
     * @throws InterruptedException InterruptedException
     * @throws ExecutionException ExecutionException
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        sync().set("c", "c");
        sync().set("a", "a");
        sync().set("ac", "aacc");
        async().get("a")
                .thenApply(v -> {
                    System.out.println(v);
                    return v;
                })
                .thenCombine(async().get("c"), (a, c) -> {
                    System.out.println(a + c);
                    return a + c;
                })
                .thenCompose(v -> {
                    System.out.println(v);
                    return async().get(v);
                })
                .thenAccept(v -> {
                    System.out.println(v);
                })
                .thenRun(() -> {
                    System.out.println("run");
                });
        System.out.println("sync==" + sync().get("a"));
        Thread.sleep(1000);

    }

}
