package fun.hereis.code.hotrefresh.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author weichunhe
 * created at 2021/1/22
 */
@Component
public class BA {

    @Autowired
    private A a;

    public String hello(){
        return  a.hello()+ "; hello, I'm 3 B！";
    }
}
