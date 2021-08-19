package devx.app.licensee.common;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

import devx.app.licensee.R;
import devx.app.licensee.common.utils.HelperMethods;
import devx.app.licensee.common.utils.SharedPrefsUtils;
import devx.app.licensee.common.utils.ShareprefConstantKeys;
import devx.app.seller.webapi.response.CommonResponse;
import io.github.tonnyl.light.Light;

import static devx.app.licensee.common.LogUtil.logD;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public final String rupee = "₹ ";
    public final int SUCCESS = 1, ERROR = 2, WARNING = 3, INFO = 4;
    final private String LTAG = "DevLog:";
    private Dialog loadingDialog;
    private ConnectivityManager connectivityManager;

    public static String getUserId() {
        return SharedPrefsUtils.getStringPreference(TrailerApplication.getApplicationInstance().getApplicationContext(),
                ShareprefConstantKeys.USER_ID, "-1");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        loadingDialog = new ProgressDialog(this, R.style.MyTheme);
        loadingDialog.setCancelable(false);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    public boolean hasConnectivity() {
        connectivityManager.getActiveNetworkInfo();
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    protected static String safeText(String s) {
        if (s == null) return "";
        return s;
    }

    protected static String safeText(String s, String defaultStr) {
        if (s == null) return defaultStr;
        if (s.isEmpty()) return defaultStr;
        return s;
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    protected void gotoActivity(Class<?> classfile) {
        startActivity(new Intent(this, classfile));
    }

    public Context getContext() {
        return this;
    }

    public void closeKeyBoard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    public void openKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openKeyBoard(View editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        closeKeyBoard();
    }

    public void showSnackbar(CharSequence message) {
        if (message != null && message.toString().contains("MyInterceptor")) {
            message = "Request failed.. Something went wrong..";
        }
        showSnackbar(this.findViewById(android.R.id.content), message, 0);
    }

    protected void showSnackbar(View view, CharSequence message, @DrawableRes int icon) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    protected void showSnackbarWithAction(View view, CharSequence message, String actionTxt, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(view, message, 10000);
        snackbar.setAction(actionTxt, onClickListener);
        snackbar.show();
    }

    public void showToast(String message) {
        if (getContext() != null && message != null && !message.isEmpty()) {
            HelperMethods.materialToast(getContext(), message, MDToast.TYPE_INFO);
        }
    }

    public void showDevMessage(String msg) {
        logD(LTAG + " " + msg);
    }

    public void exit() {
        finish();
    }

    public void showDebugToast(String message) {
        if (getContext() != null && message != null && !message.isEmpty()) {
            HelperMethods.debugToast(getContext(), message);
        }
    }

    public String getStringText(int stringId) {
        return ResourcesUtil.getString(stringId);
    }

    public void showProgressDialog() {
        showProgressDialog("");
    }

    public void showProgressDialog(String message) {
        try {
            if (loadingDialog != null) {
                closeKeyBoard();
                if (!loadingDialog.isShowing())
                    loadingDialog.show();
            }
        } catch (Exception e) {
        }
    }

    public void dismissProgressDialog() {
        try {
            if (!isFinishing() && loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            try {
                loadingDialog.dismiss();
            } catch (Exception ee) {
            }
        }
    }

    public boolean invisible(View v) {
        v.setVisibility(View.INVISIBLE);
        return v.getVisibility() == View.INVISIBLE;
    }

    public boolean visible(View v) {
        v.setVisibility(View.VISIBLE);
        if (v instanceof TextInputEditText) {
            v.requestFocus();

        }
        return v.getVisibility() == View.VISIBLE;
    }

    public boolean gone(View v) {
        v.setVisibility(View.GONE);
        return v.getVisibility() == View.GONE;
    }

    public boolean isGuest() {
        return HelperMethods.getUserId().equalsIgnoreCase("-1");
    }

    public String getResString(int resourceId) {
        return getResources().getString(resourceId);
    }

    public void mToast(String s, int type) {
        if (getContext() != null) {
            HelperMethods.materialToast(getContext(), s, type);
        }
    }

    public void mToastLong(String s, int type) {
        if (getContext() != null) {
            MDToast.makeText(getContext(), s, MDToast.LENGTH_LONG, type).show();
        }
    }

    public void snack(View v, String message, int type) {
        if (v == null) return;
        switch (type) {
            case SUCCESS:
                Light.success(v, message, Light.LENGTH_SHORT);
                break;
            case ERROR:
                Light.error(v, message, Light.LENGTH_SHORT);
                break;
            case INFO:
                Light.info(v, message, Light.LENGTH_SHORT);
                break;
            case WARNING:
                Light.warning(v, message, Light.LENGTH_SHORT);
                break;
        }
    }

    public void error_snack(View v, String message) {
        snack(v, message, ERROR);
    }

    public void info_snack(View v, String message) {
        snack(v, message, INFO);
    }

    public void warning_snack(View v, String message) {
        snack(v, message, WARNING);
    }

    public void success_snack(View v, String message) {
        snack(v, message, SUCCESS);
    }

    public void noInternetToast() {
        showToast("No Internet");
    }

    public interface OnGeocodeLookup {
        void onGeocodeLookup(@Nullable List<Address> addressList);
    }

    public boolean isAllOk() {
        if (isFinishing() || isDestroyed()) return false;
        return true;
    }

    public void handleError(Throwable e) {
        String response = null;
        try {
            response = ((HttpException) e).response().errorBody().string();

            if (response != null && !response.isEmpty()) {
                CommonResponse commonResponse = new Gson().fromJson(response, CommonResponse.class);
                showToast(commonResponse.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showToast("Something went wrong!!");
        }
    }
}
