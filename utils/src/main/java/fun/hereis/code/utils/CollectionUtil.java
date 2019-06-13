package fun.hereis.code.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 集合工具类
 *
 * @author weichunhe
 * created at 2018/12/3
 */
public class CollectionUtil {
    public interface Pick<T> {
        String pick(T item);
    }

    /**
     * 摘取列表数据中的部分数据
     *
     * @param pick
     * @return
     */
    public static List<String> pick(Collection collection, Pick pick) {
        List<String> items = new ArrayList<>();
        if (collection != null && !collection.isEmpty()) {
            collection.forEach(item -> {
                items.add(pick.pick(item));
            });
        }
        return items;
    }
}
