package fun.hereis.code.rocketmq;

import org.apache.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService;
import org.apache.rocketmq.client.impl.consumer.ConsumeMessageService;
import org.apache.rocketmq.client.impl.consumer.ProcessQueue;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.ConsumeMessageDirectlyResult;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 并发消费者装饰器
 * 新增消费线程池调整功能
 *
 * @author weichunhe
 * created at 2019/8/13
 */
public class ConsumeMessageConcurrentlyServiceDecorator implements ConsumeMessageService {
    private static final Logger log = ClientLogger.getLog();

    private ConsumeMessageService service;

    private ThreadPoolExecutor consumeExecutor;

    private String consumerGroup;

    public ConsumeMessageConcurrentlyServiceDecorator(ConsumeMessageService service, String consumerGroup) {
        this.consumerGroup = consumerGroup;
        this.service = service;
        accessConsumeExecutor();

    }

    private void accessConsumeExecutor() {
        try {
            Field executorField = ConsumeMessageConcurrentlyService.class.getDeclaredField("consumeExecutor");
            executorField.setAccessible(true);
            consumeExecutor = (ThreadPoolExecutor) executorField.get(service);
        } catch (Exception e) {
            log.warn("Can't access to consumeExecutor", e);
        }
    }

    /**
     * 获取消费者线程池
     * @return
     */
    public ThreadPoolExecutor getConsumeExecutor() {
        return consumeExecutor;
    }

    /**
     * 获取当前活跃的线程数
     * @return
     */
    public int getActiveCount(){
        return consumeExecutor.getActiveCount();
    }

    @Override
    public void start() {
        service.start();
    }

    @Override
    public void shutdown() {
        service.shutdown();
    }

    @Override
    public void updateCorePoolSize(int corePoolSize) {
        service.updateCorePoolSize(corePoolSize);
    }

    @Override
    public void incCorePoolSize() {
        service.incCorePoolSize();
    }

    @Override
    public void decCorePoolSize() {
        service.decCorePoolSize();
    }

    @Override
    public int getCorePoolSize() {
        return service.getCorePoolSize();
    }

    @Override
    public ConsumeMessageDirectlyResult consumeMessageDirectly(MessageExt msg, String brokerName) {
        return service.consumeMessageDirectly(msg, brokerName);
    }

    @Override
    public void submitConsumeRequest(List<MessageExt> msgs, ProcessQueue processQueue, MessageQueue messageQueue, boolean dispathToConsume) {
        service.submitConsumeRequest(msgs, processQueue, messageQueue, dispathToConsume);
    }
}
