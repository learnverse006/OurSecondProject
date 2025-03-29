package util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String INIT_VECTOR = "RandomInitVector"; // 16 bytes IV

    public static String encrypt(String value, String key) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encrypted, String key) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(original);
    }
}

