package fun.hereis.code.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fun.hereis.code.utils.cache.GuavaCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * @author weichunhe
 * created at 2019/7/22
 */
public class TestGuavaCache {


    @Test
    public void test() {
        String value = "cache";
        GuavaCache<String> cache = GuavaCache.asyncGuavaCache(1, () -> value);
        Assert.assertEquals("缓存失败", value, cache.get());

        GuavaCache<List<String>> cacheList = GuavaCache.asyncGuavaCache(1, () -> Lists.newArrayList(value));
        Assert.assertEquals("列表缓存失败", value, cacheList.get().get(0));

        GuavaCache<Set<String>> cacheSet = GuavaCache.asyncGuavaCache(1, () -> Sets.newHashSet(value));
        Assert.assertTrue("集合缓存失败", cacheSet.get().contains(value));

        cache = GuavaCache.asyncGuavaCache(1, () -> Long.toString(System.currentTimeMillis()));
        String v = cache.get();
        System.out.println(v);
        Assert.assertEquals("初始化失败", v, v);
        cache.refresh();
        String v2 = cache.get();
        Assert.assertTrue("刷新失败", !v.equals(v2));

    }

    @Test
    public void testAsyncRefresh() {
        GuavaCache<String> cache = GuavaCache.asyncGuavaCache(1, () -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Long.toString(System.currentTimeMillis());
        });
        String v = cache.get();
        System.out.println(v);
        Assert.assertEquals("初始化失败", v, v);
        cache.refresh();
        String v2 = cache.get();
        System.out.println(v2);
        Assert.assertTrue("刷新失败", !v.equals(v2));
    }

    @Test
    public void testException() {
        GuavaCache<String> cache = GuavaCache.asyncGuavaCache(1, () -> {
            throw new RuntimeException("异常了");
        });
        String v = cache.get();
        System.out.println("============="+v);
        Assert.assertEquals("初始化失败", v, v);
    }

}
