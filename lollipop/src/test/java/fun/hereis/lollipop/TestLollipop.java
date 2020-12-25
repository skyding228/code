package fun.hereis.lollipop;

import fun.hereis.lollipop.Lettuce;
import fun.hereis.lollipop.Lollipop;
import fun.hereis.lollipop.estimate.AllInOneLevel;
import fun.hereis.lollipop.estimate.BitMapLevel;
import fun.hereis.lollipop.estimate.IntSetLevel;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试
 *
 * @author weichunhe
 * created at 2020/12/3
 */
public class TestLollipop {

    static int[] prefixes = {130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 145, 147, 149, 150, 151, 152, 153, 155, 156, 157, 158, 159, 170, 172, 175, 176, 177, 178, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 197, 198};

    private static ExecutorService executorService = Executors.newFixedThreadPool(prefixes.length);

    public static void main(String[] args) throws IOException {
        while (true) {


            BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("请选择需要测试的数量等级：1 AllInOne 2 IntSet 3 BitMap ,其他退出");
            String level = strin.readLine();

            System.out.println("选择了：" + level);

            System.out.println("请输入测试数据量：");
            String size = strin.readLine();
            System.out.println("开始执行数据量：" + size);
            switch (level.trim()) {
                case "1":
                    test(Integer.valueOf(size.trim()), AllInOneLevel.MAX_SIZE);
                    break;
                case "2":
                    test(Integer.valueOf(size.trim()), IntSetLevel.MAX_SIZE);
                    break;
                case "3":
                    test(Integer.valueOf(size.trim()), BitMapLevel.MAX_SIZE);
                    break;
                default:
                    return;
            }

        }
    }

    private static void test(int total, int estimatedSize) {
        int perfixSize = total / prefixes.length;
        int step = 100000000 / perfixSize;
        Lettuce lettuce = new Lettuce("localhost:6379", "");
        Lollipop lollipop = new Lollipop(lettuce, "lollipop", estimatedSize);
        CountDownLatch latch = new CountDownLatch(prefixes.length);
        for (int i = 0; i < prefixes.length; i++) {
            final int prefix = prefixes[i];
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < perfixSize; j++) {
                        String phone = prefix * 100000000L + j * step + "";
                        lollipop.put(phone);
//                        assertTrue(lollipop.contain(phone), phone + "包含判断异常");
//                        assertTrue(!lollipop.contain(Long.valueOf(phone) - 1 + ""), phone + "不包含判断异常");
                    }
                    latch.countDown();
                    System.out.println("剩余任务：" + latch.getCount());
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finished");
    }


    public static void assertTrue(boolean expr, String falseMsg) {
        if (!expr) {
            System.out.println(falseMsg);
            throw new RuntimeException(falseMsg);
        }
    }

    @Test
    public void testNameListGroup() {
        Lettuce lettuce = new Lettuce("a.hereis.fun:6379", "");
        NameListGroup group = new NameListGroup(lettuce);

        String phone = "19837107475";
        lettuce.sync().del("_nl_" + phone, new IntSetLevel().getFullKey("lollipop", phone), new BitMapLevel().getFullKey("lollipop", phone));
        Assert.assertFalse("不包含", group.contain(phone, 1));
        Assert.assertTrue("放入成功", group.put(phone, 1));
        Assert.assertTrue("重复放入成功", !group.put(phone, 1));
        Assert.assertTrue("包含", group.contain(phone, 1));

        Assert.assertFalse("不包含", group.contain(phone, 2));
        Assert.assertTrue("放入成功", group.put(phone, 2));
        Assert.assertTrue("重复放入成功", !group.put(phone, 2));
        Assert.assertTrue("包含", group.contain(phone, 2));
        Assert.assertFalse("不包含", group.contain(phone, 11));

        Lollipop intset = new Lollipop(lettuce, "lollipop", IntSetLevel.MAX_SIZE);
        Lollipop bitmap = new Lollipop(lettuce, "lollipop", BitMapLevel.MAX_SIZE);
        Assert.assertFalse("不包含", intset.contain(phone));
        Assert.assertTrue("放入成功", intset.put(phone) == 1);
        Assert.assertTrue("重复放入成功", intset.put(phone) == 0);
        Assert.assertTrue("包含", intset.contain(phone));

        Assert.assertFalse("不包含", bitmap.contain(phone));
        Assert.assertTrue("放入成功", bitmap.put(phone) == 1);
        Assert.assertTrue("重复放入成功", bitmap.put(phone) == 0);
        Assert.assertTrue("包含", bitmap.contain(phone));

    }


}