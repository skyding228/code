package fun.hereis.lollipop.estimate;

import java.util.Arrays;
import java.util.List;

/**
 * 估计策略，根据估计量选择对应的等级
 *
 * @author weichunhe
 * created at 2020/12/3
 */
public class EstimateStrategy {

    private static List<EstimatedLevel> LEVELS = Arrays.asList(new AllInOneLevel(), new IntSetLevel(), new BitMapLevel());

    /**
     * 根据预估数量获取具体的等级
     *
     * @param estimatedSize
     * @return
     */
    public static EstimatedLevel get(int estimatedSize) {
        for (EstimatedLevel level : LEVELS) {
            if (((EstimatedLevelCheck) level).check(estimatedSize)) {
                return level;
            }
        }
        return LEVELS.get(0);
    }
}
