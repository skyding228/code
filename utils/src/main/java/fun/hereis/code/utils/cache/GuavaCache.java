package fun.hereis.code.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * guava 异步刷新
 *
 * @author weichunhe
 * created at 2019/7/22K
 */
public class GuavaCache<V> {

    public interface Loader<V> {
        V load();
    }

    private Loader<V> loader;
    private LoadingCache<String, V> cache;
    private static final String KEY = "CACHE_KEY";

    public V get() {
        return cache.getIfPresent(KEY);
    }

    private GuavaCache(LoadingCache<String, V> cache, Loader<V> loader) {
        this.cache = cache;
        cache.put(KEY, loader.load());
    }

    /**
     * 刷新缓存的执行器
     */
    private static Executor executor = new ThreadPoolExecutor(3, 10, 10, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(3),
            new ThreadFactoryBuilder().
                    setNameFormat("GuavaCache-%d").
                    setDaemon(false).setPriority(Thread.NORM_PRIORITY).build(),
            new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 创建一个异步刷新的缓存,里面只有一个缓存值，直接调用get方法返回数据
     *
     * @param refreshAfterMinutes
     * @param loader
     * @param <V>
     * @return
     */
    public static <V> GuavaCache<V> asyncGuavaCache(long refreshAfterMinutes, Loader<V> loader) {
        LoadingCache<String, V> loadingCache = asyncRefreshCache(refreshAfterMinutes, new CacheLoader<String, V>() {
            @Override
            public V load(String key) throws Exception {
                return loader.load();
            }
        });
        return new GuavaCache(loadingCache, loader);
    }



    /**
     * 创建一个异步刷新的缓存,key类型为string
     *
     * @param refreshAfterMinutes
     * @param loader
     * @param <V>
     * @return
     */
    public static <V> LoadingCache<String, V> asyncRefreshCache( long refreshAfterMinutes, CacheLoader<String, V> loader) {
        return CacheBuilder.newBuilder().refreshAfterWrite(refreshAfterMinutes, TimeUnit.MINUTES).build(CacheLoader.asyncReloading(loader, executor));
    }


    /**
     * 创建一个同步刷新的缓存,key类型为string
     *
     * @param refreshAfterMinutes
     * @param loader
     * @param <V>
     * @return
     */
    public static <V> LoadingCache<String, V> syncRefreshCache( long refreshAfterMinutes, CacheLoader<String, V> loader) {
        return CacheBuilder.newBuilder().refreshAfterWrite(refreshAfterMinutes, TimeUnit.MINUTES).build(loader);
    }

}
