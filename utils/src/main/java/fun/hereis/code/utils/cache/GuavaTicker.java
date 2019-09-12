package fun.hereis.code.utils.cache;

import com.google.common.base.Ticker;

import java.time.Duration;

/**
 * 主要用于缓存测试
 * @author weichunhe
 * created at 2019/9/12
 */
public class GuavaTicker extends Ticker {
    /**
     * 比当前时间偏移的纳秒数，正数就是以后，负数就是之前
     */
    private long nanoOffset = 0;


    /**
     * Returns the number of nanoseconds elapsed since this ticker's fixed point of reference.
     */
    @Override
    public long read() {
        return systemTicker().read() + nanoOffset;
    }

    /**
     * 修改时钟值
     * @param duration 向前或者向后走的时间
     */
    public void tick(Duration duration) {
        nanoOffset = duration.toNanos();
    }
}
