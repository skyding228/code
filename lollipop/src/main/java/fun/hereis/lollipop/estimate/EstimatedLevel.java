package fun.hereis.lollipop.estimate;

import fun.hereis.lollipop.Lettuce;

/**
 * 预估等级
 *
 * @author weichunhe
 * created at 2020/12/3
 */
public interface EstimatedLevel {
    /**
     * 生成redis中的key
     *
     * @param phone11
     * @return
     */
    String getKey(String phone11);

    /**
     * 获取redis中需要存储的实际值
     *
     * @param phone11
     * @return
     */
    String getValue(String phone11);

    /**
     * 存入手机号
     *
     * @param lettuce   redis客户端
     * @param namespace key命名空间
     * @param phone11   11位手机号
     * @return 是否存入成功
     */
    default boolean put(Lettuce lettuce, String namespace, String phone11) {
        return lettuce.sync().sadd(getFullKey(namespace, phone11), getValue(phone11)) > 0;
    }

    /**
     * 是否包含手机号
     *
     * @param lettuce   redis客户端
     * @param namespace key命名空间
     * @param phone11   11位手机号
     * @return 是否包含
     */
    default boolean contain(Lettuce lettuce, String namespace, String phone11) {
        return lettuce.sync().sismember(getFullKey(namespace, phone11), getValue(phone11));
    }

    /**
     * 生成完整的key
     *
     * @param namespace 命名空间
     * @param phone11   11位手机号
     * @return 实际的key 值
     */
    default String getFullKey(String namespace, String phone11) {
        return namespace + getKey(phone11);
    }
}
