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
     * NOTE：the real execution time must shorter than the value, otherwise it is possible that more than one method is executing at the same time
     * @return time seconds
     */
    long timeoutSeconds() default 300;

    /**
     * the minimal execution period of the method with the annotation ,if the real execution time is faster than the value, it will wait until the time exceed. otherwise, it will be the real execution time;
     * <p>sometimes, the method execute too fast,if the time difference between different nodes is bigger than the execution time ,maybe the annotation can't work correctly.</p>
     * @return
     */
    long minExecuteSeconds() default 1;
}
