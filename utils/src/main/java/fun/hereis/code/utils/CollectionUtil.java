package fun.hereis.code.utils;

import java.util.*;
import java.util.function.Function;

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
     * @param pick 从对象中提取字符串的lambda表达式
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

    /**
     * 合并多个map
     * @param maps
     * @return
     */
    public static Map<String, String> mergeMap(Map<String, String>... maps) {
        Map<String, String> result = new HashMap<>();
        for (Map<String, String> map : maps) {
            result.putAll(map);
        }
        return result;
    }

    /**
     * 从两个map中取值，当第一个map中不存在时返回第二个map中的值
     *
     * @param key
     * @param map
     * @param defaultMap
     * @return
     */
    public static String getValue(String key, Map<String, String> map, Map<String, String> defaultMap) {
        String result = map.get(key);
        if (result == null) {
            result = defaultMap.get(key);
        }
        return result;
    }

    /**
     * 判断集合是否为空或null
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 把list转换成map对象
     * 从list中的单个对象使用getKey方法获取key，然后把对象作为value，
     *
     * @param list
     * @param getKey
     * @param <K>
     * @param <V>
     * @return 返回map，不会返回null
     */
    public static <K, V> Map<K, V> fromList(List<V> list, Function<V, K> getKey) {
        Map<K, V> map = new HashMap<>();
        if (isEmpty(list)) {
            return map;
        }
        list.forEach(item -> {
            map.put(getKey.apply(item), item);
        });
        return map;
    }

    /**
     * 如果不存在此key时添加
     *
     * @param map
     * @param key
     * @param val
     */
    public static void putIfAbsent(Map<String, String> map, String key, String val) {
        if (map != null && !map.containsKey(key)) {
            map.put(key, val);
        }
    }
}
