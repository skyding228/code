package fun.hereis.code.rocketmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

import javax.annotation.Resource;
import java.lang.annotation.*;

/**
 * @author weichunhe
 * created at 2019/8/15
 */
@Autowired
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketmqProducer {

    @AliasFor(annotation = Qualifier.class,value = "value")
    String topic() default "MY_PRODUCER";
}


