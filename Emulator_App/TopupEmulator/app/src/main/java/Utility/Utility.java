package Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ducdmse61486 on 10/4/2016.
 */
//Encrypt

//
public class Utility {
    public static String getBoughtDateString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dataVersion = df.format(new Date());
        return dataVersion;
    }

    //Get Version number
    public static String getDataVersion() {
        DateFormat df = new SimpleDateFormat("ddMMyyyyhhmmss");
        String dataVersion = df.format(new Date());
        return dataVersion;
    }

    //End version
    //encrypt
    //Read NDEF message
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    final String keyAES = "ssshhhhhhhhhhh!!!!";
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String[] getCardDataFromEncryptedString(String cardData) {
        String decryptedCardData = decrypt(cardData, keyAES);
        String result[] = decryptedCardData.split("[|]");
        String data[] = new String[2];
        int i = 0;
        for (String r : result) {
            data[i] = r;
            i++;
        }
        return data;
    }

    public String readNDEFMessage(Tag tag) {
        Ndef ndef = Ndef.get(tag);

        try {
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            if (ndefMessage != null) {
                NdefRecord[] records = ndefMessage.getRecords();
                if (records != null) {
                    for (NdefRecord ndefRecord : records) {
                        if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                            try {
                                byte[] payload = ndefRecord.getPayload();

                                // Get the Text Encoding
                                String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

                                // Get the Language Code
                                int languageCodeLength = payload[0] & 0063;

                                // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                                // e.g. "en"

                                // Get the Text
                                String result = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                                if (result != null) {
                                    return result;
                                }
                            } catch (UnsupportedEncodingException e) {

                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    public static String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    //End encrypt
    //Write NFC
    public static NdefRecord createRecord(String text) throws
            UnsupportedEncodingException {

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

    public boolean writeCard(String text, Tag tag) throws
            IOException, FormatException {
        String encryptedString = Utility.encrypt(text, keyAES);

        try {
            NdefRecord[] records = new NdefRecord[]{Utility.createRecord(encryptedString)};
            NdefMessage message = new NdefMessage(records);

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
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
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //End writeNFC
    static InputStream is = null;


    // constructor
    public Utility() {

    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public JSONObject getJSONFromUrl(String apiUrl) {
        // Making HTTP request
        JSONObject jObj = null;
        String json = "";
        try {
            URL url = new URL(apiUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);

            urlConnection.setConnectTimeout(10000);
//            urlConnection.setRequestProperty("User-Agent", "");
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setDoInput(true);
            try {
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

                }
            } catch (Exception e) {

            }

        } catch (Exception e) {
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


}
