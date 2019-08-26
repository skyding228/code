package fun.hereis.code.rocketmq;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * mq 统计分析工具类
 *
 * @author weichunhe
 * created at 2019/8/12
 */
public class MqStatistic {

    private volatile static ConcurrentHashMap<String /*id of consumer,such as consumerGroup or topicName*/, ConsumerStatistic> consumerStatisticMap = new ConcurrentHashMap<>();

    /**
     * 消费了一条消息，更新统计信息
     *
     * @param key id of consumer,such as consumerGroup or topicName
     */
    public static void incrementConsumeCount(String key) {
        ConsumerStatistic statistic = consumerStatisticMap.get(key);
        if (statistic != null) {
            statistic.incrementConsumeCount();
        }
    }

    /**
     * 初始化统计信息
     * @param topic
     * @param consumerGroup
     * @param executor
     */
    public static void initConsumerStatistic(String topic,String consumerGroup,ThreadPoolExecutor executor){
        if (StringUtils.isEmpty(topic)) {
            topic = consumerGroup;
        }
        Assert.notNull(consumerGroup,"消费者组标识不可为空");
        if(consumerStatisticMap.containsKey(consumerGroup)){
           return;
        }
        synchronized (consumerStatisticMap){
            if(consumerStatisticMap.containsKey(consumerGroup)){
               return;
            }
            consumerStatisticMap.putIfAbsent(consumerGroup,new ConsumerStatistic(topic,consumerGroup,executor));
        }
    }

    /**
     * 单例模式
     */
    private MqStatistic() {
    }
}
