package cg.yunbee.cn.wangyoujar.keycode;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import cg.yunbee.cn.wangyoujar.keycode.BASE64Decoder;
import cg.yunbee.cn.wangyoujar.keycode.BASE64Encoder;

public class DecodeKey {  
	   
    private final static String DES = "DES";  
    // 算法名称/加密模式/填充方式  
    public static final String CIPHER_ALGORITHM_ECB = "DES/ECB/PKCS5Padding";  
    public static final String CIPHER_ALGORITHM_CBC = "DES/CBC/PKCS5Padding";
       
    /** 
     * Description 根据键值进行加密 
     * @param data  
     * @param key  加密键byte数组 
     * @return 
     * @throws Exception 
     */  
    public static String encrypt(String data, String key) throws Exception {
    	if(data == null) return null;
    	if("".equals(data)) return "";
        byte[] bt = encrypt(data.getBytes(), key.getBytes());  
        String strs = new BASE64Encoder().encode(bt);  
        return strs;  
    }  
   
    /** 
     * Description 根据键值进行解密 
     * @param data 
     * @param key  加密键byte数组 
     * @return 
     * @throws IOException 
     * @throws Exception 
     */  
    public static String decrypt(String data, String key) throws IOException,  
            Exception {  
        if (data == null)  
            return null;  
        if("".equals(data)) return "";
        BASE64Decoder decoder = new BASE64Decoder();  
        byte[] buf = decoder.decodeBuffer(data);  
        byte[] bt = decrypt(buf,key.getBytes());  
        return new String(bt);  
    }  
   
    /** 
     * Description 根据键值进行加密 
     * @param data 
     * @param key  加密键byte数组 
     * @return 
     * @throws Exception 
     */  
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {  
        // 生成一个可信任的随机数源  
        SecureRandom sr = new SecureRandom();  
   
        // 从原始密钥数据创建DESKeySpec对象  
        DESKeySpec dks = new DESKeySpec(key);  
   
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);  
        SecretKey securekey = keyFactory.generateSecret(dks);  
   
        // Cipher对象实际完成加密操作  
        Cipher cipher = Cipher.getInstance(DES);  
   
        // 用密钥初始化Cipher对象  
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);  
   
        return cipher.doFinal(data);  
    }  
       
       
    /** 
     * Description 根据键值进行解密 
     * @param data 
     * @param key  加密键byte数组 
     * @return 
     * @throws Exception 
     */  
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {  
        // 生成一个可信任的随机数源  
        SecureRandom sr = new SecureRandom();  
   
        // 从原始密钥数据创建DESKeySpec对象  
        DESKeySpec dks = new DESKeySpec(key);  
   
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);  
        SecretKey securekey = keyFactory.generateSecret(dks);  
   
        // Cipher对象实际完成解密操作  
        Cipher cipher = Cipher.getInstance(DES);  
   
        // 用密钥初始化Cipher对象  
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);  
   
        return cipher.doFinal(data);  
    }  
    
    
    /** 
     * Description 根据键值进行解密 
     * @param data 
     * @param key  加密键byte数组 
     * @return 
     * @throws IOException 
     * @throws Exception 
     */  
    public static String decryptKey(String data, String key) throws IOException,  
            Exception {  
        if (data == null)  
            return null;  
        BASE64Decoder decoder = new BASE64Decoder();  
        byte[] buf = decoder.decodeBuffer(data);  
        byte[] bt = decryptKey(buf,key.getBytes());  
        return new String(bt);  
    }  
    
    /** 
     * Description 根据键值进行解密 
     * @param data 
     * @param key  加密键byte数组 
     * @return 
     * @throws Exception 
     */  
    private static byte[] decryptKey(byte[] data, byte[] key) throws Exception {  
    	 byte[] ivBytes = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
         
         AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes); 
   
        // 从原始密钥数据创建DESKeySpec对象  
        DESKeySpec dks = new DESKeySpec(key);  
   
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);  
        SecretKey securekey = keyFactory.generateSecret(dks);  
   
        // Cipher对象实际完成解密操作  
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);  
   
        // 用密钥初始化Cipher对象  
        cipher.init(Cipher.DECRYPT_MODE, securekey, ivSpec);  
   
        return cipher.doFinal(data);  
    }
      
      
    /** 
     * Description 获取字符串MD5值 
     * @param sourceStr 
     */  
    private static String MD5(String sourceStr) {  
        String result = "";  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(sourceStr.getBytes());  
            byte b[] = md.digest();  
            int i;  
            StringBuffer buf = new StringBuffer("");  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
            result = buf.toString();  
            // System.out.println("MD5(" + sourceStr + ",32) = " + result);  
            // System.out.println("MD5(" + sourceStr + ",16) = " +  
            // buf.toString().substring(8, 24));  
        } catch (NoSuchAlgorithmException e) {  
        }  
        return result;  
    }  
      
      
    /*public static void main(String[] args) throws Exception {  
        String data = "{devType:\"1\",Sys:\"01\",Name:\"张三\",PoId:\"000002\",TarPho:\"15527609770\",Desc:\"张三偷窃\"}";  
        String key = "12ttt679";//秘钥  
        String encode = encrypt(data, key);  
        System.err.println(encode);  
        String dcode = decrypt(encode, key);  
        System.err.println(dcode);  
    }  */
}  
