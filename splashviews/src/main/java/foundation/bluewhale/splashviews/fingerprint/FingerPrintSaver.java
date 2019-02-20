package foundation.bluewhale.splashviews.fingerprint;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FingerPrintSaver {
    private static final String PREF_File = "FingerprintSaver";

    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();

        editor.apply();
    }

    public static final String USE_FINGERPRINT = "USE_FINGERPRINT";

    public static boolean isUseFingerPrint(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        return pref.getBoolean(USE_FINGERPRINT, false);
    }

    public static void setUseFingerPrint(Context context, boolean useFingerPrint) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(USE_FINGERPRINT, useFingerPrint);
        editor.apply();
    }

    public static final String ENCRYPTED_KEY = "ENCRYPTED_KEY";

    public static String getEncryptedKey(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        return pref.getString(ENCRYPTED_KEY, "000000");
    }

    public static void setEncryptedKey(Context context, String encryptedKey) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ENCRYPTED_KEY, encryptedKey);
        editor.apply();
    }

    public static final String RANDOM_IV = "RANDOM_IV";

    public static String getRandomIv(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        return pref.getString(RANDOM_IV, null);
    }

    public static void setRandomIv(Context context, String randomIv) {
        SharedPreferences pref = context.getSharedPreferences(PREF_File, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(RANDOM_IV, randomIv);
        editor.apply();
    }
}
