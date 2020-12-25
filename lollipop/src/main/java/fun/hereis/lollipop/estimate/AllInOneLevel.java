package fun.hereis.lollipop.estimate;

/**
 * @author weichunhe
 * created at 2020/12/3
 */
public class AllInOneLevel implements EstimatedLevel, EstimatedLevelCheck {
    /**
     * 抛弃allInOne
     */
    public static int MAX_SIZE = 0;

    @Override
    public String getKey(String phone11) {
        return "";
    }

    @Override
    public String getValue(String phone11) {
        return phone11;
    }


    @Override
    public boolean check(int estimatedSize) {
        return false;
    }
}
