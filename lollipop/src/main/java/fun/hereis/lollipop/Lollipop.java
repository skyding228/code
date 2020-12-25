package fun.hereis.lollipop;

import fun.hereis.lollipop.estimate.EstimateStrategy;
import fun.hereis.lollipop.estimate.EstimatedLevel;

/**
 * 用于手机号redis存储的棒棒糖算法类
 *
 * @author weichunhe
 * created at 2020/12/03
 */
public class Lollipop {

    /**
     * redis 客户端
     */
    private Lettuce lettuce;
    /**
     * redis命名空间，即key前缀
     */
    private String namespace;
    /**
     * 预估等级
     */
    private EstimatedLevel estimatedLevel;

    /**
     * 创建
     *
     * @param lettuce       redis连接客户端
     * @param namespace     命名空间，即key前缀
     * @param estimatedSize 预估元素量
     */
    public Lollipop(Lettuce lettuce, String namespace, int estimatedSize) {
        this.lettuce = lettuce;
        this.namespace = namespace;
        this.estimatedLevel = EstimateStrategy.get(estimatedSize);
    }

    /**
     * 向名单库添加手机号
     *
     * @param phone11s 11位手机号
     * @return 新添加成功的数量，去除已存在的
     */
    public int put(String... phone11s) {
        int count = 0;
        for (int i = 0; i < phone11s.length; i++) {
            count += estimatedLevel.put(lettuce, namespace, phone11s[i]) ? 1 : 0;
        }
        return count;
    }

    /**
     * 判断名单库是否包含手机号
     *
     * @param phone11 11位手机号
     * @return true or false
     */
    public boolean contain(String phone11) {
        return estimatedLevel.contain(lettuce, namespace, phone11);
    }

}
