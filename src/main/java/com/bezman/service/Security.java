package com.bezman.service;

import com.bezman.Reference.Reference;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

/**
 * Created by Terence on 12/22/2014.
 */
@Service
@SuppressWarnings("deprecation")
public class Security {

    public String hash(String item) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update(item.getBytes());

            byte[] bytes = messageDigest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}
