package devx.app.licensee.common;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.google.android.libraries.places.api.Places;

import org.json.JSONObject;

import devx.app.licensee.common.utils.Constants;
import devx.app.seller.webapi.response.MyProfile.UserObj;
import devx.app.seller.webapi.response.rentals.details.TrailerDetailsResp;
import devx.app.seller.webapi.response.trailerslist.Trailers;

public class TrailerApplication extends Application {

    public static TrailerApplication application;
    public static Trailers trailerObj;
    public static UserObj userObj;
    public static JSONObject trailerDetailsResponse;


    public static synchronized TrailerApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Stetho.initializeWithDefaults(this);

        Places.initialize(getApplicationContext(), Constants.GOOGLE_MAP_KAY);
    }

    public static TrailerApplication getApplicationInstance() {
        if (application != null)
            return application;
        return null;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
