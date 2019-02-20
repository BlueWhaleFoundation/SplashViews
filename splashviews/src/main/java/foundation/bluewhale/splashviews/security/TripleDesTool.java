package foundation.bluewhale.splashviews.security;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class TripleDesTool {
    public static String encrypt(String securityKey, String message) throws Exception {
        long time = System.currentTimeMillis();

        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(securityKey
                .getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8; ) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = message.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        String encryptedString = Base64.encodeToString(cipherText, Base64.DEFAULT);

        Log.e("splash_jni", "encrypt time: " + (System.currentTimeMillis() - time));
        return encryptedString;
    }

    public static String decrypt(String securityKey, String message) throws Exception {
        return decrypt(securityKey, Base64.decode(message, Base64.DEFAULT));
    }

    public static String decrypt(String securityKey, byte[] message) throws Exception {

        long time = System.currentTimeMillis();

        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(securityKey
                .getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8; ) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        // final byte[] encData = new
        // sun.misc.BASE64Decoder().decodeBuffer(message);
        final byte[] plainText = decipher.doFinal(message);

        String result = new String(plainText, "UTF-8");

        Log.e("splash_jni", "decrypt time: " + (System.currentTimeMillis() - time));
        return result;
    }
}
