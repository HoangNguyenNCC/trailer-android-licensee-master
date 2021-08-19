package devx.app.seller.webapi

import android.util.Log
import devx.app.licensee.common.utils.ApiConstantKey
import devx.app.licensee.common.utils.HelperMethods
import okhttp3.Interceptor
import okhttp3.Response

open class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response
//        try {
        val original = chain.request()
        val originalHttpUrl = original.url
        val url = originalHttpUrl.newBuilder().build()
        val requestBuilder = original.newBuilder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader(ApiConstantKey.HEADER_AUTHORIZATION, HelperMethods.getAuthorizationToken())
            originalResponse = chain.proceed(requestBuilder.build())
            return originalResponse
//        } catch (e: Exception) {
//            e.printStackTrace()
////            return originalResponse
//        }
//        catch (e2: Error) {
//            Log.e("", "ROME parse error2: " + e2.toString());
////            return originalResponse
//        }
//        return originalResponse
    }

    companion object {
        const val LOG_TAG = "TRACE:: "
    }
}


//---------------------------------------------------------------

/*

0 = "Content-Length"
1 = "0"
2 = "Host"
3 = "dev-gtw1.coutloot.com"
4 = "Connection"
5 = "Keep-Alive"
6 = "Accept-Encoding"
7 = "gzip"
8 = "User-Agent"
9 = "okhttp/3.12.0"
10 = "Content-Type"
11 = "application/json; charset=utf-8"
12 = "Appapi-Id"
13 = "FZUx9ZBqMLpJ18lcnaEfcA7rTJOVpERbRiEuLqp1lj8="
14 = "Device-Id"
15 = "ed0369bb8a4c18d"
16 = "User-Id"
17 = "72"
18 = "Session-Id"
19 = "9708aa80f8604c8307f9aee146e4ae7dd94fb5b5dbceb11b6f1b549464fc6fe37648081aadedf9e515d89640dbf01176"
20 = "Device-Os"
21 = "Android"
22 = "Device-Name"
23 = "Unknown Android SDK built for x86_64"
24 = "Login-Token"
25 = "87703f22c7965ba3a330dcd5e0be565b706206665e15f2afebdc6f52b36aa2aa9a2e101618df5b2a15d8843e6793ccb3"
26 = "App-Version-Code"
27 = "1015"
28 = "App-Version-Name"
29 = "5.10.5"
30 = "Android-OS-Version"
31 = "5.0.2"

*/