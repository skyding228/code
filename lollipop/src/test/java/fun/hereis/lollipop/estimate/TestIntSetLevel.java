package fun.hereis.lollipop.estimate;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author weichunhe
 * created at 2020/12/25
 */
public class TestIntSetLevel {
    private IntSetLevel setLevel = new IntSetLevel();
    @Test
    public void test(){
        check("19837107475","198371070","475");
        check("19837107000","198371070","0");
        check("19837107010","198371070","10");
        check("19837107499","198371070","499");
        check("19837107500","198371075","500");
        check("19837107601","198371075","601");
        check("19837107999","198371075","999");
    }

    private void check(String phone,String key,String value){
        Assert.assertEquals(key,setLevel.getKey(phone));
        Assert.assertEquals(value,setLevel.getValue(phone));
    }
}
