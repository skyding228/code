package fun.hereis.code.spring.hotrefresh;

import java.lang.annotation.*;

/**
 * 热加载之后执行对应的方法
 * @author weichunhe
 * created at 2019/9/2
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface AfterHotLoad {
}
