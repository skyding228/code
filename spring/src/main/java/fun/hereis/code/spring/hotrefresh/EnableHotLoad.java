package fun.hereis.code.spring.hotrefresh;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 配置热加载
 * @author weichunhe
 * created at 2019/9/2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HotLoadConfig.class)
@Documented
public  @interface EnableHotLoad {
}
