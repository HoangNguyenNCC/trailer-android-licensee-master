package devx.app.licensee.webapi;

import android.content.Context;


import java.io.IOException;

import devx.app.licensee.common.utils.HelperMethods;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("authorization", HelperMethods.getAuthorizationToken())
                .addHeader("Cookie", "User-Access-Token="+HelperMethods.getAuthorizationToken())
                .build();
        return chain.proceed(request);
    }
}