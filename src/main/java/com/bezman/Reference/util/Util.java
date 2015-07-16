package com.bezman.Reference.util;

import com.bezman.Reference.EmailThread;

import java.util.Map;
import java.util.Random;

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

}
