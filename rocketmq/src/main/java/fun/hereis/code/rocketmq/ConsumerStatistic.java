package fun.hereis.code.rocketmq;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.LongAdder;

/**
 * 消费者统计信息
 * @author weichunhe
 * created at 2019/8/14
 */
public class ConsumerStatistic {
    /**
     * 记录消费总数，使用longAdder支持并发更新
     */
    LongAdder count = new LongAdder();

    String topicName;

    /**
     * 需要保留的记录数
     */
    final int tpsRecordsCount = 3;

    /**
     * tps数据
     */
    long[] tpsRecords = new long[tpsRecordsCount];
    /**
     * 构造环形数据结构，指向头元素
     */
    int head = 0;

    ThreadPoolExecutor executor;
}
