package fun.hereis.code.hotrefresh.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author weichunhe
 * created at 2021/1/22
 */
@Component
public class CA {

    @Autowired
    private BA b;

    public String hello(){
        return  b.hello()+ "; hello, I'm local C！";
    }

    public static void main(String[] args) {
        System.out.println(BA.class.getName());
    }
}
