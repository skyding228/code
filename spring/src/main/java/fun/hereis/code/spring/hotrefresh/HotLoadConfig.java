package fun.hereis.code.spring.hotrefresh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

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
}
