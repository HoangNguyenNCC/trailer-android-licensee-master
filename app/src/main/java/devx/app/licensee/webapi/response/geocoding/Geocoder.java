package devx.app.licensee.webapi.response.geocoding;

import android.os.Handler;
import android.os.Looper;

//import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import devx.app.licensee.common.TrailerApplication;
import devx.app.licensee.webapi.response.geocoding.Constants.ResponseStatuses;
import devx.app.seller.webapi.MyInterceptor;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class Geocoder {

    protected String key;
    protected String language;

    protected Geocoder(String key) {
        this.key = key;
    }

    protected static HttpUrl.Builder getDefaultUrlBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json");
    }

    protected abstract HttpUrl.Builder getUrlBuilder();


    public void fetch(final Callback callback) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new MyInterceptor())
                .build();
        ///
                //.addInterceptor(new ChuckInterceptor(TrailerApplication.getInstance().getApplicationContext()))

        HttpUrl.Builder url = getUrlBuilder();
        if (language != null) url.addQueryParameter("language", language);
        if (key != null) url.addQueryParameter("key", key);

        Request request = new Request.Builder()
                .url(url.build())
                .build();

        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                handler.post(() -> callback.onFailed(null, e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String json = response.body().string();

                try {
                    final Response geoResponse = new Response(new JSONObject(json));

                    if (geoResponse.getStatus().equals(ResponseStatuses.OK)) {
                        handler.post(() -> callback.onResponse(geoResponse));
                    } else {
                        handler.post(() -> callback.onFailed(geoResponse, new IOException()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailed(null, new IOException());
                }
            }
        });
    }
}
