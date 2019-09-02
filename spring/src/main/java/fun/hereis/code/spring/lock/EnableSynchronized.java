package fun.hereis.code.spring.lock;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 配置分布式锁
 * @author weichunhe
 * created at 2019/9/2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SynchronizedConfig.class)
@Documented
public  @interface EnableSynchronized {
}
