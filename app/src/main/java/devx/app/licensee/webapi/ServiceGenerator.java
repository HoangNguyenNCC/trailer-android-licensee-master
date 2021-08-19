package devx.app.licensee.webapi;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import devx.app.licensee.common.utils.ConstantApi;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServiceGenerator {

    private static Retrofit createLIVERetrofit() {

        return new Retrofit.Builder()
                .client(createOkHttpClient())
                .baseUrl(ConstantApi.LIVE_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static ITrailerApi getLiveEndNode() {
        return createLIVERetrofit().create(ITrailerApi.class);
    }

    public static Retrofit getApiService() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(ConstantApi.LIVE_BASE_URL).build();
    }


    private static OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(300, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS);
        //MyInterceptor myInterceptor = new MyInterceptor();

        httpClient.addNetworkInterceptor(new StethoInterceptor());
        httpClient.addNetworkInterceptor(new HeaderInterceptor());
        httpClient.retryOnConnectionFailure(true);
        return httpClient.build();
    }
    public static Retrofit getRetrofit(){

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        return new Retrofit.Builder()
                .baseUrl(ConstantApi.LIVE_BASE_URL)
                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public static Api getApi(){
        return  getRetrofit().create(Api.class);
    }
}
