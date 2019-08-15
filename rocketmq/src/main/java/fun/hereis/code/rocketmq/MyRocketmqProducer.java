package fun.hereis.code.rocketmq;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 包含统计功能的自定义实现的生产者
 * @author weichunhe
 * created at 2019/8/15
 */
@Component
@RocketmqProducer(topic = "abc")
public class MyRocketmqProducer {

    private String topic;
    /**
     *
     * @param topic
     * @param msg
     * @param key
     * @param expireAt
     */
    public void send(String topic,String msg,String key,long expireAt){

    }

    public void send(String msg,String key,long expireAt){

    }
}
