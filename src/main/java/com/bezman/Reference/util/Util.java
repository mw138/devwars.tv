package com.bezman.Reference.util;

import com.bezman.Reference.EmailThread;
import com.bezman.Reference.Reference;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Terence on 12/22/2014.
 */
public class Util {

    static Random random = new Random();

    public static String randomText(int length) {
        String allChars = "abcdefghijklmnopqrstuvwxyz0123456789";

        String randomText = "";
        for (int i = 0; i < length; i++) {
            randomText += allChars.charAt(random.nextInt(allChars.length()));
        }

        return randomText;
    }

    public static String randomNumbers(int length) {
        String allChars = "0123456789";

        String randomText = "";
        for (int i = 0; i < length; i++) {
            randomText += allChars.charAt(random.nextInt(allChars.length()));
        }

        return randomText;
    }

    public static void sendEmail(String username, String password, String subject, String message, String recipient) {
        EmailThread emailThread = new EmailThread(username, password, subject, message, recipient, false);

        emailThread.start();
    }

    public static void sendEmail(String subject, String message, String recipient) {
        sendEmail(Reference.getEnvironmentProperty("emailUsername"), Reference.getEnvironmentProperty("emailPassword"), subject, message, recipient);
    }

    public static void sendEmailHTML(String username, String password, String subject, String message, String recipient) {
        EmailThread emailThread = new EmailThread(username, password, subject, message, recipient, true);

        emailThread.start();
    }

    public static Map overwriteMap(Map oldMap, Map newMap) {
        for (Object key : oldMap.keySet()) {
            Object oldValue = oldMap.get(key);
            Object newValue = newMap.get(key);

            if (newValue != null) {
                if (newValue instanceof Map && oldValue instanceof Map) {
                    oldMap.put(key, overwriteMap((Map) oldValue, (Map) newValue));
                } else {
                    oldMap.put(key, newValue);
                }
            }
        }

        return oldMap;
    }

    public static void zipFolder(String path, File directory, ZipOutputStream zipOutputStream) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                zipFolder(path + file.getName() + File.separator, file, zipOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                zipOutputStream.putNextEntry(new ZipEntry(path + file.getName()));

                IOUtils.copy(fileInputStream, zipOutputStream);

                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
        }

    }

    public static Object toObject(Class clazz, String value) {
        if (value.isEmpty()) {
            if (Timestamp.class == clazz) return new Timestamp(new Date().getTime());
            if (Date.class == clazz) return new Date();
        }

        if (Boolean.class == clazz) return Boolean.parseBoolean(value);
        if (Byte.class == clazz) return Byte.parseByte(value);
        if (Short.class == clazz) return Short.parseShort(value);
        if (Integer.class == clazz) return Integer.parseInt(value);
        if (Long.class == clazz) return Long.parseLong(value);
        if (Float.class == clazz) return Float.parseFloat(value);
        if (Double.class == clazz) return Double.parseDouble(value);
        if (Timestamp.class == clazz) return new Timestamp(Long.valueOf(value));
        if (Date.class == clazz) return new Date(Long.valueOf(value));

        return value;
    }
}
