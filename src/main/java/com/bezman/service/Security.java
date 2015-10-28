package com.bezman.service;

import com.bezman.Reference.Reference;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.MessageDigest;

/**
 * Created by Terence on 12/22/2014.
 */
@Service
public class Security
{

    public String hash(String item)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update(item.getBytes());

            byte[] bytes = messageDigest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++)
            {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return stringBuilder.toString();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public String encrypt(String item)
    {
        try
        {
            String encryptionKey = Reference.getEnvironmentProperty("encryptionKey");

            DESKeySpec keySpec = new DESKeySpec(encryptionKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey key = keyFactory.generateSecret(keySpec);

            BASE64Encoder base64Encoder = new BASE64Encoder();

            byte[] clearBytes = item.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return base64Encoder.encode(cipher.doFinal(clearBytes));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public String decrypt(String item)
    {
        try
        {
            String encryptionKey = Reference.getEnvironmentProperty("encryptionKey");

            DESKeySpec keySpec = new DESKeySpec(encryptionKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            SecretKey key = keyFactory.generateSecret(keySpec);

            BASE64Decoder base64Decoder = new BASE64Decoder();

            byte[] encryptedBytes = base64Decoder.decodeBuffer(item);

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(encryptedBytes), "UTF8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
