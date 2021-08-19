package devx.app.licensee.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.valdesekamdem.library.mdtoast.MDToast;

import devx.app.licensee.R;
import devx.app.licensee.common.utils.HelperMethods;
import devx.app.seller.webapi.response.CommonResponse;
import io.github.tonnyl.light.Light;

public class BaseFragment extends Fragment {

    private static final String TAG = "BaseActivity";
    public final int SUCCESS = 1, ERROR = 2, WARNING = 3, INFO = 4;
    private Dialog loadingDialog;

    public void openKeyBoard() {
        try {
            View view = this.getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
        }
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

    protected void openKeyBoard(Context context, EditText editTextSearch) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(editTextSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            editTextSearch.requestFocus();
        } catch (Exception e) {
        }
    }

    public void closeKeyBoard() {
        try {
            View view = this.getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadingDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        loadingDialog.setCancelable(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showSnackBar(CharSequence message) {
        if (getActivity() != null)
            showSnackBar(getActivity().findViewById(android.R.id.content), message);
    }

    protected void showSnackBar(View view, CharSequence message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showToast(String message) {
        if (getContext() != null)
            HelperMethods.materialToast(getContext(), message, MDToast.TYPE_INFO);
    }

    public void exit() {
        getActivity().finish();
    }

    public void showProgressDialog() {
        showProgressDialog("");
    }

    public void showProgressDialog(String message) {
        if (loadingDialog != null) {
            if (!loadingDialog.isShowing())
                loadingDialog.show();
        }
    }

    public void dismissProgressDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void snack(View v, String message, int type) {
        if (v == null) return;
        switch (type) {
            case SUCCESS:
                Light.success(v, message, Light.LENGTH_SHORT).show();
                break;
            case ERROR:
                Light.error(v, message, Light.LENGTH_SHORT).show();
                break;
            case INFO:
                Light.info(v, message, Light.LENGTH_SHORT).show();
                break;
            case WARNING:
                Light.warning(v, message, Light.LENGTH_SHORT).show();
                break;

        }
    }

    public void error_snack(View v, String message) {
        snack(v, message, ERROR);
    }

    public void info_snack(View v, String message) {
        snack(v, message, INFO);
    }

    public void info_snack_long(View v, String message) {
        Light.info(v, message, Light.LENGTH_LONG).show();
    }

    public void warning_snack(View v, String message) {
        snack(v, message, WARNING);
    }

    public void success_snack(View v, String message) {
        snack(v, message, SUCCESS);
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

    public void handleErrorWithLogout(Throwable e) {
        String response = null;
        try {
            response = ((HttpException) e).response().errorBody().string();
            int httpCode = ((HttpException) e).response().code();
            if (response != null && !response.isEmpty()) {
                if(httpCode==400){
                    HelperMethods.setIsUserLogin(false);
                    HelperMethods.setUserId("-1");
                    showToast("Session expired! Please login again.");
                    HelperMethods.closeEverythingOpenSplash(getActivity());
                    return;
                }
                CommonResponse commonResponse = new Gson().fromJson(response, CommonResponse.class);
                showToast(commonResponse.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showToast("Something went wrong!!");
        }
    }
}

