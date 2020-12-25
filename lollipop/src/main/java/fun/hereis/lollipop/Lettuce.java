package fun.hereis.lollipop;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
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
 * redis 客户端
 *
 * @author weichunhe
 * created at 2020-12-03
 */
public class Lettuce {

    private boolean isCluster = false;

    private StatefulRedisClusterConnection<String, String> clusterConnection;

    private StatefulRedisConnection<String, String> connection;

    /**
     * 创建客户端
     *
     * @param servers  服务地址,多个节点用,连接；如果是集群请至少填写两个节点以上
     * @param password 连接密码
     */
    public Lettuce(String servers, String password) {
        Set<HostAndPort> nodes = getNodes(servers);
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
                    .enablePeriodicRefresh(true)
                    .enableAllAdaptiveRefreshTriggers()
                    .build();

            clusterClient.setOptions(ClusterClientOptions.builder()
                    .socketOptions(SocketOptions.builder().keepAlive(true).build())
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .build());
            clusterConnection = clusterClient.connect();
        }
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
     * @return
     */
    private static Set<HostAndPort> getNodes(String nodeAddrs) {
        String config = nodeAddrs;

        String[] addresses = config.replaceAll(" +", "").split(",");
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (String address : addresses) {
            if (StringUtils.isNotEmpty(address)) {
                hostAndPorts.add(HostAndPort.parse(address));
            }
        }
        return hostAndPorts;
    }

}
