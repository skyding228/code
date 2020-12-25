package fun.hereis.lollipop.estimate;

import fun.hereis.lollipop.Lettuce;

/**
 * 期望使用bitmap存储
 *
 * @author weichunhe
 * created at 2020/12/3
 */
public class BitMapLevel implements EstimatedLevel, EstimatedLevelCheck {
    /**
     * key/value的分割点
     */
    private static int SplitIndex = 6;

    public static int MAX_SIZE = Integer.MAX_VALUE;

    @Override
    public String getKey(String phone11) {
        return phone11.substring(0, SplitIndex);
    }

    @Override
    public String getValue(String phone11) {
        return phone11.substring(SplitIndex);
    }

    @Override
    public boolean put(Lettuce lettuce, String namespace, String phone11) {
        return lettuce.sync().setbit(getFullKey(namespace, phone11), Long.valueOf(getValue(phone11)), 1) == 0;
    }

    @Override
    public boolean contain(Lettuce lettuce, String namespace, String phone11) {
        return lettuce.sync().getbit(getFullKey(namespace, phone11), Long.valueOf(getValue(phone11))) == 1;
    }

    @Override
    public boolean check(int estimatedSize) {
        return estimatedSize <= MAX_SIZE;
    }
}
