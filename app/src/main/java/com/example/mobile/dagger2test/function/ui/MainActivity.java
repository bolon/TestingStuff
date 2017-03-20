package com.example.mobile.dagger2test.function.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobile.dagger2test.App;
import com.example.mobile.dagger2test.R;
import com.example.mobile.dagger2test.dependency.modules.custencryption.EncryptionUtil2;
import com.example.mobile.dagger2test.dependency.modules.network.ConnectionInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    @Named("interceptorOff")
    ConnectionInterface connectionInterface;

    byte[] salt, iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getApplicationComp().inject(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("test1", "yee");
        editor.apply();

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int resultCode = 104;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions.toArray(new String[permissions.size()]), resultCode);
        }

        findViewById(R.id.btn1).setOnClickListener(v -> ((TextView) findViewById(R.id.text2)).setText("Just another test"));
    }

    private void doTestHere() {}

    private void doDecryptionCustom() {
        try {
            String encryptedFilename = "test.vdo";
            String key = "aqwesderwsdqwerf";
            InputStream inFile = getApplicationContext().getAssets().open(encryptedFilename);

            EncryptionUtil2.setKey(key);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void doEncryption(ENCRYPTION_TYPE type) {
        try {
            //TODO : SALT & IV SHOULD BE STORED IN EXTERNAL DIRECTORY

            /**
             * get the secret key to get iv
             */
            SecretKeyFactory factory = provideSecretKeyFactory();
            SecretKey secretKey = provideSecretKey(factory, salt);
            Cipher cipher = provideCipher(secretKey);
            Timber.i("IV in enc " + String.valueOf(cipher.getIV()));
            Timber.i("Secret in enc " + secretKey.getEncoded());
            Timber.i("Salt in enc " + salt);


            AlgorithmParameters params = provideAlgorithmParam(cipher);
            /**
             * iv adds randomness to the text and just makes the mechanism more secure
             * used while initializing the cipher
             * file to store the iv
             */
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            // file to be encrypted
            String fileName = "anah.png";
            String fileEncryptedName = getStringHash(fileName);

            if (type == ENCRYPTION_TYPE.GBR) {
                fileEncryptedName = fileEncryptedName.concat(".gbr");
            } else if (type == ENCRYPTION_TYPE.MSK) {
                fileEncryptedName = fileEncryptedName.concat(".msk");
            }

            InputStream inFile = getApplicationContext().getAssets().open(fileName);
            // encrypted file
            String resultFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + getApplicationContext().getPackageName() + File.separator + fileEncryptedName;

            String resultFilePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + getApplicationContext().getPackageName() + File.separator + "apalah.ini";

            Timber.i("Hash result : " + getStringHash(fileName));

            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + getApplicationContext().getPackageName() + File.separator;

            File fileDirPath = new File(dirPath);
            File file = new File(resultFilePath);
            File file2 = new File(resultFilePath2);

            createFile(fileDirPath, file);
            file2.createNewFile();

            if (file.exists()) {
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

                outFile.flush();
                outFile.close();

                OutputStream outFile2 = new FileOutputStream(file2);
                byte[] input2 = new byte[inFile.available()];

                while ((bytesRead = inFile.read(input)) != -1) {
                    byte[] output2 = cipher.update(input2, 0, bytesRead);
                    if (output2 != null)
                        outFile2.write(output2);
                }

                byte[] output2 = cipher.doFinal();
                if (output2 != null) {
                    outFile2.write(output2);
                }


                inFile.close();
                outFile2.flush();
                outFile2.close();

                doDecryption();
            } else {
                throw new FileNotFoundException("No file exist, check permission or check whtawever dude");
            }
        } catch (IOException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException e) {
            Timber.e(e.getMessage());
            e.printStackTrace();
        }
    }

    private String getStringHash(String text) {
        int hash = 3;
        String salt = "DYN3";
        for (int i = 0; i < text.length(); i++) {
            hash = hash * 31 + text.charAt(i);
        }

        return String.valueOf(hash);
    }

    /**
     * do the decryption with same salt & iv
     */
    void doDecryption() {
        try {
            SecretKeyFactory factory = provideSecretKeyFactory();
            SecretKey secret = provideSecretKey(factory, salt);
            Cipher cipher = provideDecryptCipher(secret);
            Timber.i("IV in dec " + String.valueOf(cipher.getIV()));
            Timber.i("Secret in dec " + secret.getEncoded());
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + getApplicationContext().getPackageName() + File.separator;
            Timber.i("Salt in dec " + salt);


            FileInputStream fis = null;
            File outputFile;
            FileOutputStream fos = null;

            File files[] = new File(dirPath).listFiles();

            byte[] in;
            int read;
            String md5_1 = "8dd87179c96fb1f7db64a53b141e0885";
            String md5_2 = "e359cd4238e8de3780247e33b37fdc92";

            String originalFileName = "anah.png";
            /**
             * Traverse file
             */
            for (File f : files) {
                if (f.getName().contains(".gbr")) {
                    String decryptedFileName = f.getName().split("\\.")[0];
                    String hashOriginalName = getStringHash(originalFileName);

                    if (decryptedFileName.equalsIgnoreCase(hashOriginalName)) {
                        in = new byte[(int) f.length()];
                        fis = new FileInputStream(f);
                        outputFile = new File(dirPath + originalFileName);
                        fos = new FileOutputStream(outputFile);

                        while ((read = fis.read(in)) != -1) {
                            byte[] output = cipher.update(in, 0, read);
                            if (output != null)
                                fos.write(output);
                        }
                    }
                } else if (f.getName().contains(".ini")) {
                    in = new byte[(int) f.length()];
                    fis = new FileInputStream(f);
                    outputFile = new File(dirPath + "anah2.png");
                    fos = new FileOutputStream(outputFile);

                    while ((read = fis.read(in)) != -1) {
                        byte[] output = cipher.update(in, 0, read);
                        if (output != null)
                            fos.write(output);
                    }
                }
            }

            byte[] output = cipher.doFinal();
            if (output != null & fos != null) {
                fos.write(output);
                fos.flush();
                fos.close();
            }

            if (fis != null) {
                fis.close();
            }

        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private boolean checkHash(String fileName) {
        return getStringHash(fileName).equals(fileName);
    }

    void createFile(File dirFile, File file) {
        try {
            dirFile.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * salt is used for encoding
     * writing it to a file
     * salt should be transferred to the recipient securely
     * for decryption
     */
    public byte[] provideSalt() {
        byte[] salt = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return salt;
    }

    public SecretKeyFactory provideSecretKeyFactory() {
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

    public SecretKey provideSecretKey(SecretKeyFactory factory, byte[] salt) {
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

    public Cipher provideCipher(SecretKey secret) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cipher provideDecryptCipher(SecretKey secret) {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AlgorithmParameters provideAlgorithmParam(Cipher cipher) {
        return cipher.getParameters();
    }

    String providePath() {
        return getApplicationContext().getExternalCacheDir().getPath() + File.separator + getApplicationContext().getPackageName() + File.separator + "salt";
    }

    enum ENCRYPTION_TYPE {
        GBR,
        MSK
    }

}
