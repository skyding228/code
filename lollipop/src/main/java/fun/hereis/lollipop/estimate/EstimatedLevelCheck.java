package fun.hereis.lollipop.estimate;

/**
 * 判断是否符合此预估等级
 *
 * @author weichunhe
 * created at 2020/12/3
 */
public interface EstimatedLevelCheck {
    /**
     * 判断此预估值是否可以使用此等级
     *
     * @param estimatedSize
     * @return
     */
    boolean check(int estimatedSize);
}
