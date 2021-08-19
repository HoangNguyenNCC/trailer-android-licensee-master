package devx.app.licensee.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class ResourcesUtil {
    private static Context context = TrailerApplication.getInstance().getApplicationContext();

    public static Drawable getDrawableById(int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    public static String getString(int resId) {
        return SDK_INT >= LOLLIPOP ? context.getResources().getString(resId) :
                context.getResources().getString(resId);
    }

    public static String getString(int resId, Context context) {
        return context.getResources().getString(resId);
    }

    public static int getColor(int resId) {
        return ContextCompat.getColor(context, resId);
    }


    public static ColorStateList getColorStateList(int resId) {
        return SDK_INT >= LOLLIPOP ? context.getResources().getColorStateList(resId) :
                context.getResources().getColorStateList(resId);
    }


    public static float getDimen(int resId) {
        return SDK_INT >= LOLLIPOP ? context.getResources().getDimension(resId) :
                context.getResources().getDimension(resId);
    }
}
