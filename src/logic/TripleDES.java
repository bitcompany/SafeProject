package logic;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 *
 * @author Андрей
 */
public class TripleDES {
    
    public TripleDES(FileInputStream in, FileOutputStream out, String arg, String keyname) {
        try {
            try {
                Cipher c = Cipher.getInstance("DESede");
            } catch (Exception e) {
                System.err.println("Установка провайдера SunJCE");
                Provider sunjce = new com.sun.crypto.provider.SunJCE();
                Security.addProvider(sunjce);
            }
            
            File keyfile = new File(keyname);
            
            if(arg.equals("-g")) {
                System.out.println("генерация ключа");
                System.out.flush();
                SecretKey key = generateKey();
                writeKey(key, keyfile);
                System.out.println("Ключ сгенерирован");
            }
            else if(arg.equals("-e")) {
                SecretKey key = readKey(keyfile);
                encrypt(key, in, out);
            }
            else if(arg.equals("-d")) {
                SecretKey key = readKey(keyfile);
                decrypt(key, in, out);
            }
        } catch(Exception e) {
            System.err.println(e);
            System.err.println("Неверные входные данные!!!");
        }
    }
    
    public TripleDES(String arg, String filename) {
        try {
            try {
                Cipher c = Cipher.getInstance("DESede");
            } catch (Exception e) {
                System.err.println("Установка провайдера SunJCE");
                Provider sunjce = new com.sun.crypto.provider.SunJCE();
                Security.addProvider(sunjce);
            }
            
            File keyfile = new File(filename + ".key");
            
            if(arg.equals("-g")) {
                System.out.println("генерация ключа");
                System.out.flush();
                SecretKey key = generateKey();
                writeKey(key, keyfile);
                System.out.println("Ключ сгенерирован");
            }
            else {
                System.err.println("Ошибка генерации ключа");
            }
        } catch(Exception e) {
            System.err.println(e);
            System.err.println("Неверные входные данные!!!");
        }
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException{
        KeyGenerator keygen = KeyGenerator.getInstance("DESede");
        return keygen.generateKey();
    }
    
    public static void writeKey(SecretKey key, File f) 
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        DESedeKeySpec keyspec = (DESedeKeySpec)keyfactory.getKeySpec(key, DESedeKeySpec.class);
        byte[] rawkey = keyspec.getKey();
        
        FileOutputStream out = new FileOutputStream(f);
        out.write(rawkey);
        out.close();
    }
    
    public static SecretKey readKey(File f) 
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException{
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        byte[] rawkey = new byte[(int)f.length()];
        in.readFully(rawkey);
        in.close();
        
        DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        return keyfactory.generateSecret(keyspec);      
    }
    
    public static void encrypt(SecretKey key, InputStream in, OutputStream out) 
            throws NoSuchAlgorithmException, IOException, 
            InvalidKeyException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        byte[] buffer = new byte[2048];
        int byteRead;
        while ((byteRead = in.read(buffer)) != -1) {
            cos.write(buffer, 0, byteRead);
        }
        
        in.close();
        out.close();
        cos.close();
        java.util.Arrays.fill(buffer, (byte)0);
    }
    
    public static void decrypt(SecretKey key, InputStream in, OutputStream out) 
            throws NoSuchAlgorithmException, IOException, 
            InvalidKeyException, NoSuchPaddingException, 
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] buffer = new byte[2048];
        int byteRead;
        while((byteRead = in.read(buffer)) != -1) {
            out.write(cipher.update(buffer, 0, byteRead));
        }
        
        out.write(cipher.doFinal());
        out.flush();
        in.close();
        out.close();
    }
}
