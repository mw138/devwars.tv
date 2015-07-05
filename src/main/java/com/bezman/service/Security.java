package com.bezman.service;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Terence on 12/22/2014.
 */
public class Security
{

    public static String encryptionKey = "thisprobablyisntaverygoodpasswordforyousyntag";

    public static String emailUsername = "devwarssyntag@gmail.com";
    public static String emailPassword = "syntagrocks";

    public static String recaptchaPublicKey = "6LeoIgQTAAAAAMYim_-jaDVnt_0JWPH4szTzEiio";
    public static String recaptchaPrivateKey = "6LeoIgQTAAAAAE1r5L47MCymlRNwyqAnAjz0Q10K";

    public static String facebookAppID = "648751498587352";
    public static String facebookSecret = "fe32b0ea363e0714371fe95151cebfea";

    public static String googleClientID = "140697828804-s9j0bcgita2s59nof7j1cg82663ug0br.apps.googleusercontent.com";
    public static String googleSecret = "uJ_fMgcHL7XbvTfLw9qZnpLN";

    public static String twitterConsumerKey = "R2PkiCrA3PcnwHeeFMUA9xNTt";
    public static String twitterConsumerSecret = "37A18nFTUUakpgPCu4ciYruSpRRcBIEEuyQNfAWpNfwv0jbRLf";

//    public static String twitterConsumerKey = "";
//    public static String twitterConsumerSecret = "";

    public static String redditAppID = "81R9jMxVCNRKeg";
    public static String redditSecret = "qWgxOEXvxdlUU4xJnx4NMCiHMuQ";

    public static String redditAppID2 = "mqDsT6du9HQOCw";
    public static String redditSecret2 = "Z0M1NFJZloHPVrCkuBjwcIuA7fI";

    public static String twitchClientID = "gb4jruphdgkp1n3un2ur1ce3xf3ci5r";
    public static String twitchSecret = "300o2y4jiwamn5vpuihlue6a34vzawr";

    public static String twitchClientID2 = "hf6dzqucv2vr9mbxhhlnipab5o5gztf";
    public static String twitchSecret2 = "g1ywwssve3iqcyvz86y10pj2ynea54t";

    public static String githubClientID = "5c1e483f1937833090f8";
    public static String githubSecret = "ba6b9cbb06e08cb54cbb5a2991163c1ff2c58476";

    public static String githubClientID2 = "614ec2f808dd0c74e506";
    public static String githubSecret2 = "fbe1911f4cb4b9a41c2d65724a624b59533e2c7f";

    public static String firebaseToken = "ifv82eGMk6Csufs8Tr01Prkf2IBMgOmHgMQDPp5k";

    public static String encrypt(String item)
    {
        try
        {
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

    public static String decrypt(String item)
    {
        try
        {
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
