package fun.hereis.code.spring;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * redis 客户端，基于lettuce依赖包
 *
 * @author weichunhe
 * created at 19-8-17
 */
public class Redis {

    private boolean isCluster = false;

    private StatefulRedisClusterConnection<String, String> clusterConnection;

    private StatefulRedisConnection<String, String> connection;

    /**
     * 创建实例
     *
     * @param addr     多个地址用,分割, 只有1个地址时会被认为单节点，集群地址请至少传递两个地址
     * @param password 集群密码，没有可传null
     * @return
     */
    public static Redis newInstance(String addr, String password) {
        return new Redis().init(addr, password);
    }

    private Redis init(String addr, String password) {
        Set<HostAndPort> nodes = getNodes(addr);
        if (nodes.size() == 1) {
            //get the only one host and port
            HostAndPort hostAndPort = nodes.toArray(new HostAndPort[1])[0];
            RedisURI uri = RedisURI.create(hostAndPort.getHostText(), hostAndPort.getPort());
            if (StringUtils.isNotEmpty(password)) {
                uri.setPassword(password);
            }
            io.lettuce.core.RedisClient client = io.lettuce.core.RedisClient.create(uri);
            connection = client.connect();
        } else {
            isCluster = true;
            RedisClusterClient clusterClient = getRedisClusterClient(password, nodes);
            clusterConnection = clusterClient.connect();
        }
        return this;
    }


    static RedisClusterClient getRedisClusterClient(String password, Set<HostAndPort> nodes) {
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
        return clusterClient;
    }

    private Redis() {
    }

    /**
     * @return 同步命令
     */
    public RedisClusterCommands<String, String> sync() {
        return isCluster ? clusterConnection.sync() : connection.sync();
    }

    /**
     * @return 异步命令
     */
    public RedisClusterAsyncCommands<String, String> async() {
        return isCluster ? clusterConnection.async() : connection.async();
    }

    /**
     * 获取配置节点
     *
     * @param addr 多个地址用,分割, 只有1个地址时会被认为单节点，集群地址请至少传递两个地址
     * @return
     */
    static Set<HostAndPort> getNodes(String addr) {
        String[] addresses = addr.replaceAll(" +", "").split(",");
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (String address : addresses) {
            if (StringUtils.isNotEmpty(address)) {
                hostAndPorts.add(HostAndPort.parse(address));
            }
        }
        return hostAndPorts;
    }

}
