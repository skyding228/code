package fun.hereis.lollipop.estimate;

/**
 * 期望使用intset存储
 *
 * @author weichunhe
 * created at 2020/12/3
 */
public class IntSetLevel implements EstimatedLevel, EstimatedLevelCheck {
    /**
     * key/value的分割点
     */
    private static int SplitIndex = 8, SplitCodePoint = "5".codePointAt(0);

    public static int MAX_SIZE = 1 * 10000 * 10000;


    @Override
    public String getKey(String phone11) {
        String prefix = phone11.substring(0, SplitIndex);
        return prefix + (phone11.codePointAt(SplitIndex) < SplitCodePoint ? 0 : 5);
    }

    @Override
    public String getValue(String phone11) {
        return Integer.valueOf(phone11.substring(SplitIndex)).toString();
    }

    @Override
    public boolean check(int estimatedSize) {
        return estimatedSize <= MAX_SIZE;
    }

}
