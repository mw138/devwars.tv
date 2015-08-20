package com.bezman.Reference.util;

import com.bezman.Reference.EmailThread;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Terence on 12/22/2014.
 */
public class Util
{

    static Random random = new Random();

    public static String randomText(int length)
    {
        String allChars = "abcdefghijklmnopqrstuvwxyz0123456789";

        String randomText = "";
        for (int i = 0; i < length; i++)
        {
            randomText += allChars.charAt(random.nextInt(allChars.length()));
        }

        return randomText;
    }

    public static String randomNumbers(int length)
    {
        String allChars = "0123456789";

        String randomText = "";
        for (int i = 0; i < length; i++)
        {
            randomText += allChars.charAt(random.nextInt(allChars.length()));
        }

        return randomText;
    }

    public static void sendEmail(String username, String password, String subject, String message, String recipient)
    {
        EmailThread emailThread = new EmailThread(username, password, subject, message, recipient, false);

        emailThread.start();
    }

    public static void sendEmailHTML(String username, String password, String subject, String message, String recipient)
    {
        EmailThread emailThread = new EmailThread(username, password, subject, message, recipient, true);

        emailThread.start();
    }

    public static Map overwriteMap(Map oldMap, Map newMap)
    {
        for (Object key : oldMap.keySet())
        {
            Object oldValue = oldMap.get(key);
            Object newValue = newMap.get(key);

            if (newValue != null)
            {
                if (newValue instanceof Map && oldValue instanceof Map)
                {
                    oldMap.put(key, overwriteMap((Map) oldValue, (Map) newValue));
                } else
                {
                    oldMap.put(key, newValue);
                }
            }
        }

        return oldMap;
    }

    public static void zipFolder(String path, File directory, ZipOutputStream zipOutputStream) throws IOException
    {
        for(File file : directory.listFiles())
        {
            if (file.isDirectory())
            {
                zipFolder(path + file.getName() + File.separator, file, zipOutputStream);
            } else
            {
                FileInputStream fileInputStream = new FileInputStream(file);
                zipOutputStream.putNextEntry(new ZipEntry(path + file.getName()));

                IOUtils.copy(fileInputStream, zipOutputStream);

                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
        }

    }

}
