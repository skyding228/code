package fun.hereis.code.rocketmq;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * mq 统计分析工具类
 * @author weichunhe
 * created at 2019/8/12
 */
public class MqStatistic {

    public enum TpsTrend{
        /**
         * 上升,可继续增加线程
         */
        UPWARD,
        /**
         * 下降,需要减少线程
         */
        DOWNWARD,
        /**
         * 平稳,暂不需要更改
         */
        SMOOTH;
        private TpsTrend(){}
    }

    /**
     * 消费统计数据
     */
    class ConsumeStatistic {
        /**
         * 记录消费总数，使用longAdder支持并发更新
         */
        LongAdder count = new LongAdder();

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

        /**
         * 计算tps趋势
         * @return
         */
        public TpsTrend calcTpsTrend(){
            if(head < tpsRecords.length){
                return TpsTrend.SMOOTH;
            }

            return null;
        }

    }


    /**
     * 单例模式
     */
    private MqStatistic(){}
}
