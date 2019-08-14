package fun.hereis.code.rocketmq;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者工厂
 *
 * @author weichunhe
 * created at 2019/8/14
 */
public class ConsumerFactory {

    private static Logger log = LoggerFactory.getLogger(ConsumerFactory.class);

    private static Set<Class> allowedParamTypes = Sets.newHashSet(String.class, MessageExt.class, ConsumeConcurrentlyContext.class);
    private static final String paramErrorDesc = "The method with RocketmqListener annotation can has 3 params at most.[String type message,original message MessageExt and ConsumeConcurrentlyContext]";


    /**
     * 保存已有消费者
     */
    private volatile static ConcurrentHashMap<String/*consumerGroup*/, DefaultMQPushConsumer> consumers = new ConcurrentHashMap<>();

    /**
     * @param config
     * @param method
     */
    public static void initConsumer(RocketmqListener config, Method method, Object bean, String defaultNameSrv) {
        String consumerGroup = config.consumerGroup();
        if (consumers.containsKey(consumerGroup)) {
            return;
        }
        synchronized (consumerGroup.intern()) {
            if (consumers.containsKey(consumerGroup)) {
                return;
            }
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            consumers.putIfAbsent(consumerGroup, consumer);

            if (StringUtils.isNotEmpty(config.nameSrv())) {
                consumer.setNamesrvAddr(config.nameSrv());
            } else if (StringUtils.isNotEmpty(defaultNameSrv)) {
                consumer.setNamesrvAddr(defaultNameSrv);
            }
            consumer.registerMessageListener(createListener(method, bean));
            try {
                consumer.subscribe(config.topic(), config.subExpression());
                consumer.start();
            } catch (MQClientException e) {
                log.error("An error occurs whiling initializing consumer {},{}", method, config, e);
                throw new RuntimeException(e.getMessage());
            }

            ConsumeMessageConcurrentlyServiceDecorator consumeService = new ConsumeMessageConcurrentlyServiceDecorator(consumer.getDefaultMQPushConsumerImpl().getConsumeMessageService(), consumer.getConsumerGroup());
            consumer.getDefaultMQPushConsumerImpl().setConsumeMessageService(consumeService);

            log.info("initialize consumer {} for {} successfully.", consumer, consumerGroup);
        }

    }

    private static MessageListenerConcurrently createListener(Method method, Object bean) {

        /**
         * 仅能获取到3个参数，String类型的消息，原始消息 MessageExt，及上下文信息ConsumeConcurrentlyContext
         */
        if (method.getParameterCount() > 3) {
            throw new RuntimeException(paramErrorDesc);
        }
        for (Class<?> aClass : method.getParameterTypes()) {
            if (!allowedParamTypes.contains(aClass)) {
                throw new RuntimeException(paramErrorDesc);
            }
        }
        return new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(msg -> consume(msg, context, method, bean));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        };
    }

    private static void consume(MessageExt msg, ConsumeConcurrentlyContext context, Method method, Object bean) {
        Object[] args = new Object[method.getParameterCount()];
        String msgStr = new String(msg.getBody(), StandardCharsets.UTF_8);
        Class[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (String.class.equals(types[i])) {
                args[i] = msgStr;
            } else if (MessageExt.class.equals(types[i])) {
                args[i] = msg;
            } else if (ConsumeConcurrentlyContext.class.equals(types[i])) {
                args[i] = context;
            }
        }
        try {
            method.invoke(bean, args);
        } catch (Exception e) {
            log.error("An error occurs while {} consuming {}", method, msg, e);
        }
    }

    /**
     * single tone
     */
    private ConsumerFactory() {

    }
}
