package fun.hereis.lollipop.estimate;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author weichunhe
 * created at 2020/12/25
 */
public class TestBitMap {
    private BitMapLevel bitmap = new BitMapLevel();
    @Test
    public void test(){
        check("19837107475","198371","07475");
        check("19837100000","198371","00000");
        check("19837199999","198371","99999");
    }

    private void check(String phone,String key,String value){
        Assert.assertEquals(key, bitmap.getKey(phone));
        Assert.assertEquals(value, bitmap.getValue(phone));
    }
}
