package fun.hereis.code.utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class RSAUtil {
    private static Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
    private static PublicKey publicKey = getPublicKey("MDEwDQYJKoZIhvcNAQEBBQADIAAwHQIWD6FvxDSe1TSmo+vCjKm1prkN1oTf6wIDAQAB");
    private static PrivateKey privateKey = getPrivateKey("MIGPAgEAMA0GCSqGSIb3DQEBAQUABHsweQIBAAIWD6FvxDSe1TSmo+vCjKm1prkN1oTf6wIDAQABAhYAtU5kpAO8Yl3YmIzlEtoyO/h1cBlFAgs/2GZnmFeAhjhB1QILPqyGnIrwmbxbur8CCyukAtObGWy70iaFAgswfafA4rb5G4UVXQILFhuLZfBx1cO88vg=");

    static {
        Security.addProvider(provider);
    }


    /**
     * 生成指定长度的密钥对
     *
     * @param length
     * @throws Exception
     */
    public static void gen(int length) throws Exception {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", provider);
        keyGen.initialize(length);
        KeyPair key = keyGen.generateKeyPair();
        System.out.println(Base64.getEncoder().encodeToString(key.getPublic().getEncoded()));
        System.out.println(Base64.getEncoder().encodeToString(key.getPrivate().getEncoded()));

    }

    /**
     * 从字符串转换为公钥
     * @param base64PublicKey 公钥字符串
     * @return
     */
    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", provider);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * 从字符串转换为私钥
     * @param base64PrivateKey 私钥字符串
     * @return
     */
    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA", provider);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 加密
     * @param data 原始字符串
     * @return 加密后的base64编码字符串
     */
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * @param data base64密文字符串
     * @return 原始字符串
     */
    public static String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        //1000000===149631
        String phone = "19837107475";
        Set<String> ens = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            String en = encrypt(phone);
            ens.add(en);
            System.out.println(decrypt(en) + "----" + en);
        }
        System.out.println(ens.size() + "===" + (System.currentTimeMillis() - start));

        gen(172);
    }
}

