package fun.hereis.lollipop;

/**
 * 名单库分组保存,
 * 直接用手机号作为key，value是bitmap，此手机号归属的所有分组编号对应的bit位置设置为1；
 * 所以要求，名单库的编号要是int类型，尽量的从小还是编码。新增一个名单库时只会增加对应手机号的value值的bitmap长度，比较节约内存。
 *
 * @author weichunhe
 * created at 2020/12/25
 */
public class NameListGroup {
    /**
     * 命名空间
     */
    protected static String namespace = "_nl_";

    /**
     * redis 客户端
     */
    private Lettuce lettuce;

    public NameListGroup(Lettuce lettuce) {
        this.lettuce = lettuce;
    }

    /**
     * 向名单库添加手机号
     *
     * @param phone11 11位手机号
     * @param id      名单库编号
     * @return 是否添加成功, 已存在时添加失败
     */
    public boolean put(String phone11, int id) {
        return lettuce.sync().setbit(makeKey(phone11), id, 1) == 0;
    }

    /**
     * 判断名单库是否包含手机号
     *
     * @param phone11 11位手机号
     * @param id      名单库编号
     * @return true or false
     */
    public boolean contain(String phone11, int id) {
        return lettuce.sync().getbit(makeKey(phone11), id) == 1;
    }

    private String makeKey(String phone11) {
        return namespace + phone11;
    }
}
