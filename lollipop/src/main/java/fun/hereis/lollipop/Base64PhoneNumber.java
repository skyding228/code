package fun.hereis.lollipop;

/**
 * 7位10进制型与4位64进制之间的相互转换方法，
 * 即手机号后7位压缩为最长4为字符的算法实现
 *
 * @author weichunhe
 * created at 2020/12/03
 */
@SuppressWarnings("ALL")
class Base64PhoneNumber {

    /**
     * 64进制所需要的字符
     */
    static final char[] Base64Chars = {
            //64-90 = -64 => 0-26
            '@','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            //51-55 = -24 => 27-31
            '3', '4', '5', '6', '7',
            //96-122 = -64 => 32-58
            '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            //59-63 -0
            ';', '<', '=', '>', '?'
    };


    /**
     * 将7位int型数字(手机号后7位)转换为最多4个字符的64进制表示法，
     * 省略低位的0字符，解码时末位补0至4个字符，详见解码方法
     *
     * @param number 手机号后7位
     * @return 手机号后7位的64进制
     */
    public static String encode(int number) {
        int i=-1;
        while( ++i < 3 && (number & 0x3f) == 0){ //找到前三位第一个非0字符
            number = number >> 6;
        }
        char[] chars = new char[4-i];
        for (i=0; i < chars.length; i++) {
            chars[i] =Base64Chars[number & 0x3f];
            number = number >> 6;
        }
        return new String(chars);
    }
    /**
     * 将64进制的4位字符(不够4位的末位补0)解码位7位10进制数字

     * @param base64 64进制字符串
     * @return 7位int型数字
     */
    public static int decode(String base64) {
        byte[] bytes = base64.getBytes();
        int number = 0;
        for (int i = bytes.length-1; i >=0; i--) {
            if((bytes[i] & 0b1001000) == 0){ //51-55区间 -24
                bytes[i] = (byte)((bytes[i] & 0b011111) | 0b1000);
            }else{ //其余区间减64或0
                bytes[i] = (byte)( bytes[i] & 0b0111111);
            }
            number = number << 6;
            number |= bytes[i] ;
        }
        number = number << ((4-bytes.length) *6); //末位补0至4字符
        return number;
    }

}
