package fun.hereis.code.spring.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weichunhe
 * created at 2019/9/2
 */
@Configuration
public class SynchronizedConfig {

    /**
     * register a bean to container
     * @return aop config
     */
    @Bean
    public SynchronizedAOP aop(){
        return new SynchronizedAOP();
    }
}
