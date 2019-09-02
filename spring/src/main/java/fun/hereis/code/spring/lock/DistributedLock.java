package fun.hereis.code.spring.lock;

import fun.hereis.code.spring.Lettuce;

/**
 * @author weichunhe
 * created at 18-11-27
 */
public class DistributedLock {

    private static final String LOCK_PREFIX = "DistributedLock:";

    /**
     * default timeout is 5 minutes
     */
    private static Long DEFAULT_LOCK_TIMEOUT = 5 * 60L;

    public static boolean lock(String key) {
        return lock(key, "1", DEFAULT_LOCK_TIMEOUT);
    }

    public static boolean unlock(String key) {
        return Lettuce.sync().del(getFullKey(key)) == 1;
    }

    /**
     * lock a key with a value
     *
     * @param key
     * @param value
     * @param timeoutSeconds
     * @return
     */
    public static boolean lock(String key, String value, Long timeoutSeconds) {

        String fullKey = getFullKey(key);
        boolean locked = Lettuce.sync().setnx(fullKey, value);
        if (timeoutSeconds == null || timeoutSeconds < 0) {
            timeoutSeconds = DEFAULT_LOCK_TIMEOUT;
        }
        long ttl = Lettuce.sync().ttl(fullKey);
        //must reset ttl ,because if crashed here last time,It will be locked forever.
        if (ttl < 0 || ttl > timeoutSeconds) {
            Lettuce.sync().expire(fullKey, timeoutSeconds.intValue());
        }
        return locked;
    }

    /**
     * only if the locked value equals to the param value ,you can unlock it .
     * It means that only the person who locked it can unlock it, until time out.
     *
     * @param key
     * @param value
     */
    public static boolean unlock(String key, String value) {
        String realKey = getFullKey(key);
        if (realKey == null) {
            return false;
        }
        if (!value.equals(new String(Lettuce.sync().get(realKey)))) {
            return false;
        }
        return Lettuce.sync().del(realKey) == 1;
    }

    private static String getFullKey(String key) {
        return (LOCK_PREFIX + key);
    }
}
