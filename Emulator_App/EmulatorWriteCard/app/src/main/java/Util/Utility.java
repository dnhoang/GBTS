package Util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ducdmse61486 on 10/4/2016.
 */

public class Utility  extends Application{
    final String keyAES = "ssshhhhhhhhhhh!!!!";
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),Base64.DEFAULT);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    public String[] getCardDataFromEncryptedString(String cardData){
        String decryptedCardData=decrypt(cardData, keyAES);
        String result[] = decryptedCardData.split("[|]");
        String data[]=new String[2];
        int i=0;
        for(String r : result){
            data[i]=r;
            i++;
        }
        return data;
    }
    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decode(strToDecrypt,Base64.DEFAULT)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    //End encrypt
    //Write NFC
    public static NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }


    //End writeNFC
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public Utility() {

    }
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public JSONObject getJSONFromUrl(String apiUrl) {
        // Making HTTP request
        try {
            URL url = new URL(apiUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
//            urlConnection.setRequestProperty("User-Agent", "");
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setDoInput(true);
            urlConnection.connect();

            if (urlConnection != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    //GET String JSON FROM API
                    json = stringBuilder.toString();

                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Không thể kết nối với server!", Toast.LENGTH_SHORT).show();
            }
//        }  catch (SocketTimeoutException e){
//
//                    Toast.makeText(getApplicationContext(), "Không thể kết nối với server!", Toast.LENGTH_SHORT).show();

        }catch (Exception e) {
            e.printStackTrace();
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON OBJECT
        return jObj;

    }
    public boolean writeNFCCard(String inputString, Tag mytag) {
        String encryptedString = Utility.encrypt(inputString, keyAES);

        try {
            NdefRecord[] records = new NdefRecord[]{Utility.createRecord(encryptedString)};
            NdefMessage message = new NdefMessage(records);

            Ndef ndef = Ndef.get(mytag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(mytag);
                if (ndefFormatable != null) {
                    // initialize tag with new NDEF message

                    try {
                        ndefFormatable.connect();
                        ndefFormatable.format(message);
                    } finally {
                        try {
                            ndefFormatable.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (FormatException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
