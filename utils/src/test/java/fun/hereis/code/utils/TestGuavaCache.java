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

        GuavaCache<Set<String>> cacheSet = GuavaCache.asyncGuavaCache( 1, () -> Sets.newHashSet(value));
        Assert.assertTrue("集合缓存失败",cacheSet.get().contains(value));
    }

}
