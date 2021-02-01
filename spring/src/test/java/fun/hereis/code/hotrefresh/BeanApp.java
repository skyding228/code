package fun.hereis.code.hotrefresh;

import fun.hereis.code.hotrefresh.bean.BA;
import fun.hereis.code.hotrefresh.bean.CA;
import fun.hereis.code.hotrefresh.bean.D;
import fun.hereis.code.spring.hotrefresh.HotClassLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author weichunhe
 * created at 2021/1/22
 */
public class BeanApp {
    public static ApplicationContext applicationContext;

    public static void main(String[] args) {

        applicationContext = new AnnotationConfigApplicationContext(BA.class.getPackage().getName());

        System.out.println(((CA) applicationContext.getBean("CA")).hello());

        BA ba = HotClassLoader.reload(BA.class, (ConfigurableApplicationContext) applicationContext,"BA");

        System.out.println(ba.hello());

        System.out.println(((CA) applicationContext.getBean("CA")).hello());


        ba = HotClassLoader.reload(BA.class, (ConfigurableApplicationContext) applicationContext,"BA");

        System.out.println(ba.hello());

        System.out.println(((CA) applicationContext.getBean("CA")).hello());


        CA ca = HotClassLoader.reload(CA.class, (ConfigurableApplicationContext) applicationContext,"CA");
        System.out.println(ca.hello());

        System.out.println(((CA) applicationContext.getBean("CA")).hello());


        ba = HotClassLoader.reload(BA.class, (ConfigurableApplicationContext) applicationContext,"BA");

        System.out.println(ba.hello());

        System.out.println(((CA) applicationContext.getBean("CA")).hello());


        D d = HotClassLoader.reload(D.class, (ConfigurableApplicationContext) applicationContext,"d");
        System.out.println(d.hello());
        d = HotClassLoader.reload(D.class, (ConfigurableApplicationContext) applicationContext,"d");
        System.out.println(d.hello());
    }

}
