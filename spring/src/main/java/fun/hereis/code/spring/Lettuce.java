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
 * redis客户端
 * 静态方法默认读取 redis.properties 中的redis.nodes 和 redis.password 进行创建集群连接
 * @author weichunhe
 * created at 19-8-17
 */
public class Lettuce {

    private static boolean isCluster = false;

    private static StatefulRedisClusterConnection<String, String> clusterConnection;

    private static StatefulRedisConnection<String, String> connection;


    private static void init() {
        if(clusterConnection != null || connection != null){
            return;
        }
        Properties redisProps = ClasspathPropertyUtil.load("redis.properties");
        String address = redisProps.getProperty("redis.nodes", "localhost:6379");
        String password = (String) redisProps.get("redis.password");
        Set<HostAndPort> nodes = getNodes(address);
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
            clusterConnection = cluster(address,password);
        }
    }

    private Lettuce() {
    }

    /**
     * 连接一个redis集群
     * @param clusterNodes 集群地址，多个节点用,拼接
     * @param password 集群密码,无密码时为空
     * @return 集群连接，sync/async 进行同步/异步调用
     */
    public static StatefulRedisClusterConnection<String, String> cluster(String clusterNodes,String password){
        Set<HostAndPort> nodes = getNodes(clusterNodes);
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
                .enablePeriodicRefresh(true)
                .enableAllAdaptiveRefreshTriggers()
                .build();

        clusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions)
                .build());
        return clusterClient.connect();
    }

    /**
     * @return 同步命令
     */
    public static RedisClusterCommands<String, String> sync() {
        init();
        return isCluster ? clusterConnection.sync() : connection.sync();
    }

    /**
     * @return 异步命令
     */
    public static RedisClusterAsyncCommands<String, String> async() {
        init();
        return isCluster ? clusterConnection.async() : connection.async();
    }

    /**
     * 获取配置节点
     *
     * @return
     */
    private static Set<HostAndPort> getNodes( String nodes) {
        String[] addresses = nodes.replaceAll(" +", "").split(",");
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (String address : addresses) {
            if (StringUtils.isNotEmpty(address)) {
                hostAndPorts.add(HostAndPort.parse(address));
            }
        }
        return hostAndPorts;
    }

}
