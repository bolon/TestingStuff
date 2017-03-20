package com.example.mobile.dagger2test.dependency.modules.custencryption;

import android.util.Base64;

import com.google.android.things.pio.PeripheralManagerService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

/**
 * Created by mobile on 26/01/2017.
 */

public class EncryptionUtil2 {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    private static String decryptedString;
    private static String encryptedString;

    public static void setKey(String myKey) {

        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            System.out.println(key.length);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            System.out.println(key.length);
            System.out.println(new String(key, "UTF-8"));
            secretKey = new SecretKeySpec(key, "AES");


        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getDecryptedString() {
        return decryptedString;
    }

    private static void setDecryptedString(String decryptedString) {
        EncryptionUtil2.decryptedString = decryptedString;
    }

    public static String getEncryptedString() {
        return encryptedString;
    }

    public static void setEncryptedString(String encryptedString) {
        EncryptionUtil2.encryptedString = encryptedString;
    }

    public static String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);


            setEncryptedString(Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), Base64.DEFAULT));

        } catch (Exception e) {

            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setDecryptedString(new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT))));
        } catch (Exception e) {

            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static void decrypt(File f) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);



        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void androidThingsTest() {
        try {
            PeripheralManagerService service = new PeripheralManagerService();
            Timber.i("SERVICE", service.getGpioList());
        }
        catch (NoClassDefFoundError ex) {
            Timber.e("Error bruh ", ex.getMessage());
        }
    }
}
