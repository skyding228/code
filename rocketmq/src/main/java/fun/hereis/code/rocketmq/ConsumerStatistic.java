package fun.hereis.code.rocketmq;

import org.apache.rocketmq.common.ThreadFactoryImpl;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * 消费者统计信息
 * @author weichunhe
 * created at 2019/8/14
 */
public class ConsumerStatistic {
    /**
     * 线程池相关
     */
    int keepAliveMins = 5,maxThreads = 1000,minThreads = 1;
    /**
     * 记录消费总数，使用longAdder支持并发更新
     */
    LongAdder count = new LongAdder();

    String topicName;

    String consumerGroup;
    /**
     * 上次调整线程时间
     */
    private long lastAdjustAt = System.currentTimeMillis();
    /**
     * 上次tps
     */
    private long lastTps = 0;
    /**
     * 上次线程数量
     */
    private int lastThreads = minThreads;

    ThreadPoolExecutor executor;

    private ScheduledExecutorService adjustScheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    private void adjustThreads(){
        int currentThreads = executor.getActiveCount();
        //说明线程空闲时间较长，自动销毁了线程
        if(currentThreads < lastThreads){
            addAThread();
            return;
        }
        //计算tps
        long periodSeconds = (System.currentTimeMillis() - lastAdjustAt)/1000;
        long tps = (count.sumThenReset()-lastTps)/periodSeconds;
        if(tps > lastTps){
            lastTps = tps;
            addAThread();
            return;
        }
        //其他情况不能关闭线程，需要等到消费者空闲的时候线程自动减少
    }

    private void addAThread(){
        int currentThreads = executor.getActiveCount();
        currentThreads ++;
        lastThreads = currentThreads;
        executor.setCorePoolSize(lastThreads);
        executor.prestartAllCoreThreads();
    }

    public ConsumerStatistic(String topicName, String consumerGroup, ThreadPoolExecutor executor) {
        this.topicName = topicName;
        this.consumerGroup = consumerGroup;
        if(executor == null){
            executor = new ThreadPoolExecutor(minThreads,maxThreads, keepAliveMins,TimeUnit.MINUTES,new LinkedBlockingQueue<>());
        }
        this.executor = executor;
        executor.setCorePoolSize(minThreads);
        executor.setMaximumPoolSize(maxThreads);
        executor.setKeepAliveTime(keepAliveMins, TimeUnit.MINUTES);
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        executor.setThreadFactory(new ThreadFactoryImpl(consumerGroup + "_Thread_"));

        adjustScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                adjustThreads();
            }
        },30,30,TimeUnit.SECONDS);
    }

    /**
     * 消费了一条消息，更新统计信息
     */
    public void incrementConsumeCount(){
        count.increment();
    }

}
