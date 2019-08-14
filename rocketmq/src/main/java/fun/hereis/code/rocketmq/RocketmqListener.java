package fun.hereis.code.rocketmq;

import java.lang.annotation.*;

/**
 * 消费者监听器
 *
 * @author weichunhe
 * created at 2019/8/14
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketmqListener {
    /**
     * name server address,默认使用
     *
     * @return
     */
    String nameSrv() default "";

    String consumerGroup();

    /**
     * Topic name.
     */
    String topic();

    /**
     * subscription expression
     *
     * @return
     */
    String subExpression() default "*";

}
