package fun.hereis.code.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author weichunhe
 * created at 2019/4/16
 */
@Component
public class Consumer {


    @RocketmqProducer(topic = "abc")
//    @Autowired
//    @Qualifier("myRocketmqProducer")
    private MyRocketmqProducer producer;



    @RocketmqListener(topic = "TopicTest", consumerGroup = "test")
    public void consume(String msgStr, MessageExt msg) {
        System.out.println(producer);
        System.out.printf("%s Receive New Messages: %s %s %n", Thread.currentThread().getName(), msgStr, new String(msg.getBody()));
    }


    public static void main(String[] args) throws Exception {

        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("my_consumer");

        // Specify name server addresses.
        consumer.setNamesrvAddr("139.129.101.77:9876");

        // Subscribe one more more topics to consume.
        consumer.subscribe("TopicTest", "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                msgs.forEach(msg -> {
                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), new String(msg.getBody()));
                });

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();

        ConsumeMessageConcurrentlyServiceDecorator consumeService = new ConsumeMessageConcurrentlyServiceDecorator(consumer.getDefaultMQPushConsumerImpl().getConsumeMessageService(), consumer.getConsumerGroup());
        consumer.getDefaultMQPushConsumerImpl().setConsumeMessageService(consumeService);

        System.out.printf("Consumer Started.%n");

        Thread.sleep(1000000000);
    }
}
