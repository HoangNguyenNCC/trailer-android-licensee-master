package devx.app.licensee.common;

import android.content.Context;

public interface BaseView {

    void showSnackbar(CharSequence message);

    void showToast(String message);

    void exit();

    void showProgressDialog();

    void dismissProgressDialog();

    boolean hasConnectivity();

    Context getContext();

    String getResString(int resounrceId);

    void mToast(String s, int type);

    void mToastLong(String s, int type);

    void showDevMessage(String msg);

}