package fun.hereis.code.spring.lock;

import java.lang.annotation.*;

/**
 * 分布式锁
 *
 * @author weichunhe
 * created at 2018/12/5
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Synchronized {
    /**
     * the key in redis you will lock ,
     * It will use (full class name).(method name of the annotation on) by default
     *
     * @return redis key,default is (full class name).(method name of the annotation on)
     */
    String value() default "";

    /**
     * the longest time you can lock.
     * default timeout is 5 minutes
     *
     * @return time seconds
     */
    long timeoutSeconds() default 300;
}
