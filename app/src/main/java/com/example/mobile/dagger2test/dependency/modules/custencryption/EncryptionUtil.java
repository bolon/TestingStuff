package com.example.mobile.dagger2test.dependency.modules.custencryption;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mobile on 25/01/2017.
 */

public class EncryptionUtil {
    /*public static final String SALT_KEY = "SALT";
    public static final String IV_KEY = "IV";
    private byte[] salt, iv;
    private Context context;
    private SecretKeyFactory factory;
    private SecretKey secretKey;
    private Cipher cipher;
    private AlgorithmParameters params;
    private HashMap<String, String> keyPair;
    private AES_MODE aesMode;
    *//**
     * Tinydb only store sharedpref asdf
     *//*
    private TinyDB tinyDB;

    *//**
     * Constructor for encryption util
     *
     * @param context
     *//*
    public EncryptionUtil(Context context, AES_MODE aesMode) {
        this.context = context;
        this.tinyDB = new TinyDB(context);

        if (tinyDB.getBytes(SALT_KEY).length == 0) {
            byte[] toSave = provideSalt();
            tinyDB.putBytes(SALT_KEY, toSave);
            Log.d("Salt byte ", "empty");
        }

        this.salt = tinyDB.getBytes(SALT_KEY);
        this.factory = provideSecretKeyFactory();
        this.secretKey = provideSecretKey(factory, salt);
        this.cipher = provideCipher(secretKey);
        this.params = provideAlgorithmParam(cipher);
        this.aesMode = aesMode;

        if (tinyDB.getBytes(IV_KEY).length == 0) {
            byte[] toSave = provideIv(params);
            tinyDB.putBytes(IV_KEY, toSave);
            Log.d("IV byte ", "empty");
        }
        this.iv = tinyDB.getBytes(IV_KEY);

        keyPair = new HashMap<>();
    }

    public static void deleteTempFile(Context context) {
        boolean b;

        String dirPath = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context)
                + File.separator + "enc" + File.separator;

        File lastDir = new File(dirPath + "temp_folder");
    }

    public void startProcess() {
        this.execute();
    }

    private byte[] provideIv(AlgorithmParameters params) {
        try {
            return params.getParameterSpec(IvParameterSpec.class).getIV();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void doEncryption() {
        try {
            Log.d("Salt byte ", String.valueOf(salt));
            Log.d("IV byte ", String.valueOf(iv));

            String fileEncryptedName;
            Uri uri;
            InputStream inFile;

            ArrayList<String> stringList = new ArrayList<>();
            String path = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context);

            for (File f : new File(path).listFiles()) {
                stringList.add(f.getName());
            }

            // encrypted file dir
            String dirPath = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context)
                    + File.separator + "enc" + File.separator;
            File fileDirPath = new File(dirPath);
            if (!fileDirPath.exists())
                fileDirPath.mkdirs();


            //traverse
            for (String originalFileName : stringList) {
                fileEncryptedName = getStringHash(originalFileName);

                if (originalFileName.contains("mp4"))
                    fileEncryptedName = fileEncryptedName.concat(".vdo");
                else if (originalFileName.contains("png"))
                    fileEncryptedName = fileEncryptedName.concat(".gbr");
                else if (originalFileName.contains("mp3"))
                    fileEncryptedName = fileEncryptedName.concat(".msk");
                else
                    fileEncryptedName = fileEncryptedName.concat(".jny");


                uri = Uri.fromFile(new File(FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context)
                        + File.separator + originalFileName));

                keyPair.put(fileEncryptedName, originalFileName);

                // encrypted file target
                String resultFilePath = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context)
                        + File.separator + "enc" + File.separator + fileEncryptedName;

                File file = new File(resultFilePath);
                if (file.createNewFile()) {
                    inFile = context.getContentResolver().openInputStream(uri);

                    OutputStream outFile = new FileOutputStream(file);
                    //file encryption
                    byte[] input = new byte[inFile.available()];

                    int bytesRead;

                    while ((bytesRead = inFile.read(input)) != -1) {
                        byte[] output = cipher.update(input, 0, bytesRead);
                        if (output != null)
                            outFile.write(output);
                    }

                    byte[] output = cipher.doFinal();
                    if (output != null) {
                        outFile.write(output);
                    }

                    inFile.close();
                    outFile.close();

                    tinyDB.putObject("KEYPAIR", keyPair);
                } else throw new IOException("Can not create encrypted initial file");
            }
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private String getOriginalFileName(String hashText) {
        return keyPair.get(hashText);
    }

    private void doDecryption() {
        try {
            Log.d("Salt byte dec ", String.valueOf(salt));
            Log.d("IV byte dec ", String.valueOf(iv));

            Cipher cipher = provideDecryptCipher(secretKey);

            String encryptedDir = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context) + File.separator
                    + "enc" + File.separator;

            String tempDecryptDir = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context) + File.separator
                    + "enc" + File.separator + "temp";

            FileInputStream fis = null;
            File outputFile;
            FileOutputStream fos = null;

            File tempDir = new File(tempDecryptDir);
            tempDir.mkdirs();

            File files[] = new File(encryptedDir).listFiles();

            byte[] in;
            int read;

            *//**//**
             * e6a673a7a457fa5740eadb58635baa7c
             * 1126b04cb61ad028ca6b775b2434ac68
             * Traverse file
             *//**//*

            for (File f : files) {
                if (!f.isDirectory()) {

                    in = new byte[(int) f.length()];
                    in = new byte[(int) f.length()];
                    fis = new FileInputStream(f);
                    outputFile = new File(tempDecryptDir + File.separator + getOriginalFileName(f.getName()));
                    fos = new FileOutputStream(outputFile);

                    while ((read = fis.read(in)) != -1) {
                        byte[] output = cipher.update(in, 0, read);
                        if (output != null)
                            fos.write(output);
                    }
                    byte[] output = cipher.doFinal();
                    if (output != null) {
                        fos.write(output);
                        fos.close();
                    }
                }
            }

            if (fis != null) {
                fis.close();
            }

        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    void createTempFile(String originalFilename) {
        try {
            String dirPath = FileManager.getDownloadUnzipFolder(context) + File.separator + StudyPathManager.getCurrentLessonDirectory(context)
                    + File.separator + "enc" + File.separator;

            File lastDir = new File(dirPath + "temp_folder");
            lastDir.mkdir();
            File smthing = File.createTempFile("asd_" + originalFilename, ".png", lastDir);
            File smthing2 = new File(dirPath + "temp_folder" + File.separator + originalFilename + ".png");

            smthing2.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    *//**
     * salt is used for encoding
     * writing it to a file
     * salt should be transferred to the recipient securely
     * for decryption
     *//*
    private byte[] provideSalt() {
        byte[] salt = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return salt;
    }

    private SecretKeyFactory provideSecretKeyFactory() {
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            return factory;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey provideSecretKey(SecretKeyFactory factory, byte[] salt) {
        // password to encrypt the file
        String password = "somePAs$w0rD_";
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                    256);
            SecretKey secretKey = factory.generateSecret(keySpec);

            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher provideCipher(SecretKey secret) {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher provideDecryptCipher(SecretKey secret) {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

            return cipher;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AlgorithmParameters provideAlgorithmParam(Cipher cipher) {
        return cipher.getParameters();
    }

    private String getStringHash(String text) {
        int HASH_SALT_NUM = 3;
        final String HASH_SALT_STRING = "DYN3";

        for (int i = 0; i < text.length(); i++) {
            HASH_SALT_NUM = HASH_SALT_NUM * 4 + text.charAt(i);
        }

        return String.valueOf(HASH_SALT_NUM);
    }

    @Override
    protected Void doInBackground(Void... params) {
        long start, end;

        switch (aesMode) {
            case ENCRYPTION:
                start = System.currentTimeMillis();
                doEncryption();
                end = System.currentTimeMillis();
                Log.d("Duration ENC ", String.valueOf((end - start)));
                break;
            case DECRYPTION:
                start = System.currentTimeMillis();
                doDecryption();
                end = System.currentTimeMillis();
                Log.d("Duration DEC ", String.valueOf((end - start)));
                break;
        }

        return null;
    }

    *//**
     * enum var to determine which type to use when encrypting file
     *//*
    private enum ENCRYPTION_TYPE {
        GBR,
        MSK,
        VDO,
        JNY
    }*/

    public enum AES_MODE {
        ENCRYPTION,
        DECRYPTION
    }
}
