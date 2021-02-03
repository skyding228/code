package fun.hereis.code.spring.hotrefresh;

import fun.hereis.code.spring.hotrefresh.resource.DynamicResourceTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author weichunhe
 * created at 2021/1/28
 */
@Configuration
public class HotLoadConfig {
    /**
     * 暴露controller
     * @return
     */
    @Bean
    public HotLoadController controller(){
        return new HotLoadController();
    }

    @Bean
    public DynamicResourceTransformer transformer(){
        return new DynamicResourceTransformer();
    }
}
