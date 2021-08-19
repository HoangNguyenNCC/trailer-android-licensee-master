package devx.app.licensee.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefsUtils {

    public SharedPrefsUtils() {
    }

    public static HashMap<String, ArrayList<String>> getMapPreference(Context context, String key, HashMap<String, ArrayList<String>> defaultValue) {
        HashMap<String, ArrayList<String>> value = defaultValue;
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            Gson gson = new Gson();
            String arrayListString = preferences.getString(key, null);
            Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            value = gson.fromJson(arrayListString, type);
            //value = preferences.getString(key, value.toString());
        }
        return value;
    }

    public static boolean setMapPreference(Context context, String key, HashMap<String, ArrayList<String>> value) {
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String value1 = gson.toJson(value);
            editor.putString(key, value1);
            return editor.commit();
        }
        return false;
    }
    public static String getStringPreference(Context context, String key, String defaultValue) {
        String value = defaultValue;
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getString(key, value);
        }
        return value;
    }

    public static String getStringPreferenceWithDefaultValue(Context context, String key, String defaultValue) {
        String temp = getStringPreference(context, key, defaultValue);
        return temp != null ? temp : defaultValue;
    }

    public static boolean setStringPreference(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static float getFloatPreference(Context context, String key, float defaultValue) {
        float value = defaultValue;
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getFloat(key, defaultValue);
        }
        return value;
    }

    public static boolean setFloatPreference(Context context, String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(key, value);
            return editor.commit();
        }
        return false;
    }

    public static long getLongPreference(Context context, String key, long defaultValue) {
        long value = defaultValue;
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            value = preferences.getLong(key, defaultValue);
        }
        return value;
    }

    public static boolean setLongPreference(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        int value = defaultValue;
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            try {
                value = preferences.getInt(key, defaultValue);
            } catch (Exception e) {

            }
        }
        return value;
    }

    public static boolean setIntegerPreference(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
            if (preferences != null) {
                value = preferences.getBoolean(key, defaultValue);
            }
        }
        return value;
    }

    public static boolean setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences("trailer", Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    public SharedPrefsUtils getObj() {
        return this;
    }
}
