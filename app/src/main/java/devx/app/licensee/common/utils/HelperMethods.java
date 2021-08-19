package devx.app.licensee.common.utils;

import android.animation.Animator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NonNls;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import devx.app.licensee.BuildConfig;
import devx.app.licensee.R;
import devx.app.licensee.common.LogUtil;
import devx.app.licensee.common.TrailerApplication;
import devx.app.licensee.webapi.ServiceGenerator;
import devx.app.seller.modules.home.hometab.HomeActivity;
import devx.app.seller.modules.login.LoginActivity;
import devx.app.seller.modules.splash.SplashActivity;
import devx.app.seller.webapi.response.trailerslist.Trailers;

import static devx.app.licensee.common.ResourcesUtil.getString;

public class HelperMethods {

    public static final String TAG = "HelperMethods: ";
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static Animator mCurrentAnimator;


    public static  List<Trailers> getUniquesElement( List<Trailers> mTrailerList) {
        List<Trailers> noRepeat = new ArrayList<Trailers>();

        for (Trailers event : mTrailerList) {
            boolean isFound = false;
            // check if the event name exists in noRepeat
            for (Trailers e : noRepeat) {
                if (e.getTrailerModel().equals(event.getTrailerModel()) ) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);
        }
        return noRepeat;
    }
    public static String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount + " Days");

    }

    public static boolean compareDate(String date) {
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");

        boolean flag=false;
        try {
            Date dt_1 = null;
            dt_1 = objSDF.parse(objSDF.format(new Date()));

            Date dt_2 = objSDF.parse(date);

            if (dt_1.compareTo(dt_2) > 0) {
                //System.out.println("Date 1 occurs after Date 2");
                flag= false;
            } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
            else if (dt_1.compareTo(dt_2) < 0) {
                //System.out.println("Date 1 occurs before Date 2");
                flag= false;
            } // compareTo method returns the value less than 0 if this Date is before the Date argument;
            else if (dt_1.compareTo(dt_2) == 0) {
               // System.out.println("Both are same dates");
                flag= true;
            } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
            else {
               // System.out.println("You seem to be a time traveller !!");
                flag= false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }
    public static boolean compareTwoDate(String date1,String date2) {
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");

        boolean flag=false;
        try {
            Date dt_1 = null;
            dt_1 = objSDF.parse(date1);

            Date dt_2 = objSDF.parse(date2);

            if (dt_1.compareTo(dt_2) > 0) {
                //System.out.println("Date 1 occurs after Date 2");
                flag= false;
            } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
            else if (dt_1.compareTo(dt_2) < 0) {
                //System.out.println("Date 1 occurs before Date 2");
                flag= true;
            } // compareTo method returns the value less than 0 if this Date is before the Date argument;
            else if (dt_1.compareTo(dt_2) == 0) {
                // System.out.println("Both are same dates");
                flag= false;
            } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
            else {
                // System.out.println("You seem to be a time traveller !!");
                flag= false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }
    public static void isTodayPick(String date) {
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
        Date dt_1 = null;
            dt_1 = objSDF.parse(objSDF.format(new Date()));

        Date dt_2 = objSDF.parse(date);

        if (dt_1.compareTo(dt_2) > 0) {
            System.out.println("Date 1 occurs after Date 2");
        } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
        else if (dt_1.compareTo(dt_2) < 0) {
            System.out.println("Date 1 occurs before Date 2");
        } // compareTo method returns the value less than 0 if this Date is before the Date argument;
        else if (dt_1.compareTo(dt_2) == 0) {
            System.out.println("Both are same dates");
        } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
        else {
            System.out.println("You seem to be a time traveller !!");
        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToFloat(Context context, int dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                context.getResources().getDisplayMetrics());
    }


    public static boolean isEmailValid(String email) {
        if(email.length()<4) return false;
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getBitmapInto64BaseEncoded(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            String endCoded_string;
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS);
            endCoded_string = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
            return "data:image/jpeg;base64," + endCoded_string;
        }
        return null;
    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // To get the screenWidth
    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }
        return screenWidth;
    }

    public static void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }

    public static Long getDateInMillis(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        long timeInMilliseconds = 0L;
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void closeEverythingOpenHome(Context activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getApplicationContext().startActivity(intent);
        activity.startActivity(intent);
    }

    public static void closeEverythingOpenSplash(Context activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getApplicationContext().startActivity(intent);
        activity.startActivity(intent);
    }
    public static void closeEverythingOpenLogin(Context activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.getApplicationContext().startActivity(intent);
        activity.startActivity(intent);
    }

    //@To convert dp into pixel
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void showSnackbarWithText(View view, Activity activity, String message) {
        if (activity == null || view == null) {
            return;
        }
        try {
            Snackbar snackbar = Snackbar
                    .make(view, "" + message, Snackbar.LENGTH_LONG)
                    .setAction("OK", view1 -> {

                    });

            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            sbView.setBackgroundColor(ContextCompat.getColor(activity, R.color.snack_bar_normal_background));
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "" + message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void internetErrorToast(Context context) {
        materialToast(context, "You're not connected to internet", MDToast.TYPE_ERROR);
    }

    public static boolean isConnectedToInternet(Context context) {

        if (context == null) return true;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean checkInternetToast(Context context) {

        if (context == null) return true;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            HelperMethods.internetErrorToast(context);
        }
        return isConnected;
    }

    public static void showToastbar(Context context, String s) {
        if (context != null)
            HelperMethods.materialToast(context, s);
    }

    public static void showDebugToastBar(Context context, String s) {
        if (context != null) {
            if (BuildConfig.DEBUG) {
                HelperMethods.showToastbar(context, s);
            }
        }
    }

    public static void showLongToastbar(Context context, String s) {
        if (context != null)
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static boolean isAppInstalled(String packageName, Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static void takeUserToPlaystore(Context context) {

        if (context != null) {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                context.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        }
    }

    public static boolean setIsUserLogin(boolean isUserLogin) {
        return SharedPrefsUtils.setBooleanPreference(TrailerApplication.getApplicationInstance().getApplicationContext(), ShareprefConstantKeys.IS_USER_LOGIN_KEY, isUserLogin);
    }

    public static boolean isUserLogin() {
        return SharedPrefsUtils.getBooleanPreference(TrailerApplication.getApplicationInstance().getApplicationContext(), ShareprefConstantKeys.IS_USER_LOGIN_KEY, false);
    }
    public static boolean setIsOwner(boolean isUserLogin) {
        return SharedPrefsUtils.setBooleanPreference(TrailerApplication.getApplicationInstance().getApplicationContext(), ShareprefConstantKeys.IS_OWNER, isUserLogin);
    }

    public static boolean isOwner() {
        return SharedPrefsUtils.getBooleanPreference(TrailerApplication.getApplicationInstance().getApplicationContext(), ShareprefConstantKeys.IS_OWNER, false);
    }

    public static String get_user_mobile_number() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(), ShareprefConstantKeys.USER_MOBILE_NO_KEY, "");
    }

    public static String getPersonUserName() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.PERSON_USER_NAME_KEY, "");
    }

    public static void setPersonUserName(String userName) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(), ShareprefConstantKeys.PERSON_USER_NAME_KEY, userName);
    }

    public static String getUserId() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_ID, "-1");
    }





    public static String getLatitude() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getInstance().getApplicationContext(), ShareprefConstantKeys.Location_Lat, "0");
    }

    public static String getLongitude() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getInstance().getApplicationContext(), ShareprefConstantKeys.Location_Long, "0");
    }

    public static void setLatitude(Context context, String value) {
        SharedPrefsUtils.setStringPreference(context, ShareprefConstantKeys.Location_Lat, value);
    }

    public static void setLongitude(Context context, String value) {
        SharedPrefsUtils.setStringPreference(context, ShareprefConstantKeys.Location_Long, value);
    }

    public static String getUserLatitude() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getInstance().getApplicationContext(), ShareprefConstantKeys.Location_Lat, "0");
    }

    public static String getUserLongitude() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getInstance().getApplicationContext(), ShareprefConstantKeys.Location_Long, "0");
    }

    public static void setUserLatitude(Context context, String value) {
        SharedPrefsUtils.setStringPreference(context, ShareprefConstantKeys.Location_Lat, value);
    }

    public static void setUserLongitude(Context context, String value) {
        SharedPrefsUtils.setStringPreference(context, ShareprefConstantKeys.Location_Long, value);
    }
    public static void setUserId(String user_id) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_ID, user_id);
    }

    public static String get_user_email() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_EMAIL_KEY, "");
    }

    public static void set_user_email(String email) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_EMAIL_KEY, email);
    }

    public static void setUserMobileNumber(String mobileNumber) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_MOBILE_NO_KEY, mobileNumber);
    }

    public static String get_user_gender() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_GENDER_KEY, "");
    }

    public static void set_user_gender(String user_gender) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_GENDER_KEY, user_gender);
    }

    public static String get_user_dob() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_DOB_KEY, "");
    }

    public static void set_user_dob(String user_dob) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_DOB_KEY, user_dob);
    }

    public static String getAuthorizationToken() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_AUTHORIZATION_TOKEN, "");
    }

    public static void setAuthorizationToken(String authorizationToken) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_AUTHORIZATION_TOKEN, authorizationToken);
    }
    public static HashMap<String, ArrayList<String>> getACL() {
        return SharedPrefsUtils.getMapPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.ACL, null);
    }

    public static void setACL(HashMap<String, ArrayList<String>> value) {
        SharedPrefsUtils.setMapPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.ACL,value );
    }

    public static void setUserProfilePicUrl(String photoUrl) {
        SharedPrefsUtils.setStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_PROFILE_IMAGE_KEY, photoUrl);
    }

    public static String extractNumberFromString(String source) {
        StringBuilder result = new StringBuilder(100);
        for (char ch : source.toCharArray()) {
            if (ch >= '0' && ch <= '9') {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static void hideViews(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }

    public static void showViews(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.VISIBLE);
        }
    }

    public static String getBase64FromFile(Context context, File file) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytesArray = null;
        try {

            InputStream fileInputStream=context.getContentResolver().openInputStream(Uri.fromFile(file));//this was giving Filenotfound error so solved it with android 10 file picker

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            bytesArray= buffer.toByteArray();


        } catch (IOException ex) {ex.printStackTrace();
            HelperMethods.showDebugToastBar(TrailerApplication.getApplicationInstance().getApplicationContext(), ex.getLocalizedMessage());}

        catch (Exception ex) {ex.printStackTrace();

        HelperMethods.showDebugToastBar(TrailerApplication.getApplicationInstance().getApplicationContext(), ex.getLocalizedMessage());}
        if(bytesArray==null) return "err";

        return "data:application/pdf;base64," + Base64.encodeToString(bytesArray, Base64.DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public static String getBase64FromBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            String endCoded_string;
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
            endCoded_string = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
            return "data:image/jpeg;base64," + endCoded_string;
        }
        return null;
    }

    public static Bitmap getBitmapFromPath(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(path, bmOptions);
    }

    public static void materialToast(Context context, String message, int type) {
        if (context == null) return;
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, type).show();
    }

    public static boolean
    openPlaceSearch(Activity activity) {
        try {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(activity);

            activity.startActivityForResult(intent, IntentParams.REQUEST_SELECT_PLACE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showLongToastbar(TrailerApplication.getInstance().getApplicationContext(), "Place services error: " + e.getMessage());
            return false;
        }
    }

    public static void materialToast(Context context, String message) {
        if (context == null) return;
        try {
            MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
        } catch (Exception e) {
        }
    }

    public static void debugToast(Context context, String message) {
        try {
            if (!BuildConfig.DEBUG) return;
            if (context == null) return;
            MDToast.makeText(context, "DEBUG: " + message, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
            Log.d(TAG, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getInDDHHMMSS(long timediff) {
        long seconds = timediff / 1000 % 60;
        long minutes = (timediff / (1000 * 60)) % 60;
        long hours = (timediff / (1000 * 60 * 60)) % 24;
        String sec, min, hour;
        long days = TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(timediff));
        if (seconds < 10) {
            sec = "0" + seconds;
        } else {
            sec = "" + seconds;
        }
        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = "" + minutes;
        }
        if (hours < 10) {
            hour = "0" + hours;
        } else {
            hour = "" + hours;
        }

        String daysStr = "";
        if (days >= 1) {
            daysStr = days + "d :";
        }
        StringBuilder time = new StringBuilder();
        time.append(daysStr).append(hour).append("h: ").append(min).append("m: ").append(sec).append("s");
        return time.toString();
    }

    public static boolean isTimeUp(long oldTime, int daysInterval) {
//        int daysInterval = 3;
        long now = new Date(System.currentTimeMillis()).getTime();
        //86400000 - 1day in milliseconds
        long timeDiff = (86400000 * daysInterval);
        return now > oldTime + timeDiff;
    }

    public static String safeText(String s) {
        if (s == null) return "";
        return s;
    }

    public static String safeText(String s, String defaultStr) {
        if (s == null) return defaultStr;
        if (s.isEmpty()) return defaultStr;
        return s;
    }

    public static String getLat() {
        try {
            return SharedPrefsUtils.getStringPreference(TrailerApplication.getInstance().getApplicationContext(), ShareprefConstantKeys.Location_Lat, "");
        } catch (Exception e) {
            return "0";
        }
    }

    public static String getLong() {
        try {
            return SharedPrefsUtils.getStringPreference(TrailerApplication.getInstance().getApplicationContext(), ShareprefConstantKeys.Location_Long, "");
        } catch (Exception e) {
            return "0";
        }

    }

    public static ColorDrawable getGreyDrawable(Context context) {
        return new ColorDrawable(context.getResources().getColor(R.color.c2_light1_grey));
    }

    public static Uri getPathForWritingImage(Context context) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return Uri.fromFile(mediaFile);
    }

    public static void downloadImage(String url, Context context, ImageView imageView) {
        if (context != null) {
            /*RequestOptions options = new RequestOptions();
            options.dontTransform();
            options.dontAnimate();
            options.fitCenter();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);*/
            Glide.with(context)
                    .load(url)
                    .placeholder(HelperMethods.getGreyDrawable(context))
                    .error(HelperMethods.getGreyDrawable(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }
    public static void downloadImage(String url, Context context, ImageView imageView,int flag) {
        if (context != null) {
            /*RequestOptions options = new RequestOptions();
            options.dontTransform();
            options.dontAnimate();
            options.fitCenter();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);*/
            if(flag==1) {
                RequestOptions options = new RequestOptions();
                options.dontTransform();
                options.dontAnimate();
                options.fitCenter();
                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(context)
                        .setDefaultRequestOptions(options)
                        .load(url)
                        .placeholder(R.drawable.grey_account_icon)
                        .error(HelperMethods.getGreyDrawable(context))
                        .into(imageView);
            }
            else{
                Glide.with(context)
                        .load(url)
                        .placeholder(HelperMethods.getGreyDrawable(context))
                        .error(HelperMethods.getGreyDrawable(context))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
        }
    }
    public static int calculateAge(Date birthdate) {
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthdate);
        Calendar today = Calendar.getInstance();

        int yearDifference = today.get(Calendar.YEAR)
                - birth.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
            yearDifference--;
        } else {
            if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < birth
                    .get(Calendar.DAY_OF_MONTH)) {
                yearDifference--;
            }

        }
        return yearDifference;
    }
    public static Bitmap downloadBitmap(String url, Context context){
       /* Glide.with(context)
                .load("http://test.com/yourimage.jpg")
                .fitCenter()
                .into(new SimpleTarget<Bitmap>(100,100) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {

                        // do something with you bitmap

                        bitmap

                    }
                });*/
       return null;
    }
    public static void downloadImage(String url, Context context, ImageView imageView, Drawable colorDrwble) {
        if (context != null) {
            Glide.with(context)
                    .load(url)
                    .placeholder(colorDrwble)
                    .error(colorDrwble)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(imageView);
        }
    }
    public static String timeCoversion12to24(String twelveHoursTime) throws ParseException {

        //Date/time pattern of input date (12 Hours format - hh used for 12 hours)
        DateFormat df = new SimpleDateFormat("hh:mm a");

        //Date/time pattern of desired output date (24 Hours format HH - Used for 24 hours)
        DateFormat outputformat = new SimpleDateFormat("HH:mm");
        Date date = null;
        String output = null;

        //Returns Date object
        date = df.parse(twelveHoursTime);

        //old date format to new date format
        output = outputformat.format(date);
        System.out.println(output);

        return output;
    }
    public static float getDynamicWidthOfItem(Context context, int parts) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels / parts;
    }

    @NonNls
    public static String getOrdinal(int i) {
        @NonNls String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];
        }
    }

    public static boolean isDebug() {
        if (BuildConfig.DEBUG) return true;
        return !BuildConfig.BUILD_TYPE.equals("release");
    }

    public static void forceToastObject(Object response) {
        try {
            LogUtil.printObject(response);
            if (TrailerApplication.getInstance() != null) {
                String gson = new Gson().toJson(response);
                HelperMethods.showToastbar(TrailerApplication.getInstance().getApplicationContext(), gson);
            }
        } catch (Exception e) {
        }
    }

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String getHourFromMiliSecs(long milisecs) {
        StringBuffer text = new StringBuffer("");
        if (milisecs > HOUR) {
            text.append(milisecs / HOUR);
        }
        return text.toString();
    }

    /*



// TODO: this is the value in ms
long ms = 10304004543l;
StringBuffer text = new StringBuffer("");
if (ms > DAY) {
  text.append(ms / DAY).append(" days ");
  ms %= DAY;
}
if (ms > HOUR) {
  text.append(ms / HOUR).append(" hours ");
  ms %= HOUR;
}
if (ms > MINUTE) {
  text.append(ms / MINUTE).append(" minutes ");
  ms %= MINUTE;
}
if (ms > SECOND) {
  text.append(ms / SECOND).append(" seconds ");
  ms %= SECOND;
}
text.append(ms + " ms");
System.out.println(text.toString());

    * */
    public static File saveImage(final Context context, final String imageData) throws IOException {
        final byte[] imgBytesData = android.util.Base64.decode(imageData,
                android.util.Base64.DEFAULT);

        final File file = File.createTempFile("image", null, context.getCacheDir());
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                fileOutputStream);
        try {
            bufferedOutputStream.write(imgBytesData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public static String convertUTCtoNormal(String utcDateTime){
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");

        Date d = null;
        try
        {
            d = input.parse(utcDateTime);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        String formatted = output.format(d);
        Log.i("DATE", "" + formatted);
        return formatted;
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    public static String convert12hrto24Hr(String strrr){
        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        String output = null;
        try{
            //Converting the input String to Date
            date= df.parse(strrr);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
            System.out.println(output);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        return output;
    }
    public static void setRecyclerAnim(RecyclerView recyclerView, Context context, int resId) {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
        recyclerView.setLayoutAnimation(animation);
    }

    public static void setRelativeLayAnim(RelativeLayout relativeLayout, Context context, int resId) {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
        relativeLayout.setLayoutAnimation(animation);
    }
    public static boolean checktimings(String startTime, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(startTime);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
    public static String convertTimeTo24Hr(String time){
        String output = "";
        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("hh:mm aa");
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("HHmm");
        Date date = null;
        //String output = null;
        try{
            //Converting the input String to Date
            date= df.parse(time);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
            System.out.println(output);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        return output;
    }
    public static boolean checkACL(String permissionType,String priviledge){
        boolean flag=false;
        Iterator it = HelperMethods.getACL().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().toString().equalsIgnoreCase(permissionType)) {
                    ArrayList<String> tempArr = (ArrayList<String>) pair.getValue();
                    if (tempArr.contains(priviledge)) {
                        return true;
                    }
                }
            }
        return flag;
    }
    public static String getGMTDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d=null;
        try
        {
            d = format.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd");

        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //System.out.println();
        return gmtFormat.format(d);
    }
    public static String getGMTDateTime(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d=null;
        try
        {
            d = format.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd'T'SSSX");

        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //System.out.println();
        return gmtFormat.format(d);
    }
    public static String newFormtaDateTime(String inputPattern,String outputPattern,String dateTime){
        DateFormat inputTime = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        DateFormat outputTime = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        inputTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startD = null;
        try {
            startD = inputTime.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        outputTime.setTimeZone(TimeZone.getDefault());

        return outputTime.format(startD);

    }
    public static String formatDateTimeToString(String  date, String format,
                                            String timeZone) {
        // null check
        if (date == null) return null;
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // create SimpleDateFormat object with input format
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d=null;
        try
        {
            d = sdf.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        // default system timezone if passed null or empty
        if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
            timeZone = Calendar.getInstance().getTimeZone().getID();
        }
        // set timezone to SimpleDateFormat
        if(timeZone=="UTC") {
            sdf.setTimeZone(TimeZone.getDefault());
        }
        else{
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        // return Date in required format with timezone as String
        return sdf.format(d);
    }

    public static String changeDateFormat(String date) {
        String temp="";
        Date titleDate;
        try {
            titleDate =new  SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            temp=new SimpleDateFormat("MMM dd\nHH:mm").format(titleDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
