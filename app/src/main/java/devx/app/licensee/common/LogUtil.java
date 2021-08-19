package devx.app.licensee.common;

import android.util.Log;

import com.google.gson.Gson;


import devx.app.licensee.BuildConfig;
import devx.app.licensee.common.utils.HelperMethods;


public class LogUtil {

    public static void info(String Tag, String message) {
        try {
            if (BuildConfig.BUILD_TYPE.equals("release"))
                return;
            Log.d(Tag, message);
        } catch (Exception e) {
        }
    }

    public static void printObject(Object object) {
        try {
            if (BuildConfig.BUILD_TYPE.equals("release"))
                return;
            String string = new Gson().toJson(object);
            LogUtil.info("logged " + object.getClass().getSimpleName(), string);
        } catch (Exception e) {
        }

    }

    public static void printObject(String tag, Object object) {
        try {
            if (BuildConfig.BUILD_TYPE.equals("release"))
                return;
            String string = new Gson().toJson(object);
            LogUtil.info(tag, string);
        } catch (Exception e) {
        }
    }

    public static void printException(Exception exception) {
        if (BuildConfig.BUILD_TYPE.equals("release"))
            return;
        exception.printStackTrace();

    }


    public static void printException(Throwable exception) {
        try {
            if (BuildConfig.BUILD_TYPE.equals("release"))
                return;
            exception.printStackTrace();
        } catch (Exception e) {
        }

    }

    public static void logCall(String... strings) {
        if (BuildConfig.DEBUG) {
            for (String s : strings) {
                Log.d("Nw_call", s);
            }
        }
    }

    public static void toastObject(Object response) {
        try {
            LogUtil.printObject(response);
            if (BuildConfig.BUILD_TYPE.equals("release"))
                return;
            if (TrailerApplication.getInstance() != null) {
                String gson = new Gson().toJson(response);
                HelperMethods.debugToast(TrailerApplication.getInstance().getApplicationContext(), gson);
            }
        } catch (Exception e) {
        }
    }

    public static void logD(String... strings) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String s : strings) {
                builder.append(s).append("\n");
            }
            Log.i("logged", builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
