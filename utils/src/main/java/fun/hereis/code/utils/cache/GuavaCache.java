package fun.hereis.code.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 基于guava cache 封装了一层常用的缓存场景，就是异步全量刷新配置缓存。
 * 每一种配置创建一个缓存对象，缓存值就是所有的配置项。所以对于guava cache 只有一个key, value; 创建时只需要提供缓存刷新时间，及加载方法即可。
 *
 * @author weichunhe
 * created at 2019/7/22K
 */
public class GuavaCache<V> {
    /**
     * 加载缓存接口
     *
     * @param <V> 加载器
     */
    public interface Loader<V> {
        V load();
    }

    /**
     * 缓存加载器
     */
    private Loader<V> loader;
    /**
     * guava 缓存，只有一个key，value
     */
    private LoadingCache<String, V> cache;
    /**
     * 唯一的一个key值
     */
    private static final String KEY = "CACHE_KEY";

    /**
     * 获取缓存值
     *
     * @return
     */
    public V get() {
        try {
            return cache.get(KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 强制刷新缓存
     */
    public void refresh() {
        try {
            cache.put(KEY, loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造一个配置缓存
     *
     * @param cache
     * @param loader
     */
    private GuavaCache(LoadingCache<String, V> cache, Loader<V> loader) {
        this.cache = cache;
        this.loader = loader;
        refresh();
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
     * @param refreshAfterMinutes 缓存刷新时间间隔，分钟数
     * @param loader              缓存加载器
     * @param <V>                 缓存值类型
     * @return 缓存对象
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
     * @param refreshAfterMinutes 缓存刷新时间间隔，分钟数
     * @param loader              缓存加载器
     * @param <V>                 缓存值类型
     * @return 缓存对象
     */
    public static <V> LoadingCache<String, V> asyncRefreshCache(long refreshAfterMinutes, CacheLoader<String, V> loader) {
        return CacheBuilder.newBuilder().refreshAfterWrite(refreshAfterMinutes, TimeUnit.MINUTES).build(CacheLoader.asyncReloading(loader, executor));
    }

}
