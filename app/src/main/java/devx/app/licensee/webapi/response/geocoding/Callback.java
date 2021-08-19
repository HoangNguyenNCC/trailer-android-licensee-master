package devx.app.licensee.webapi.response.geocoding;


import java.io.IOException;


public interface Callback {
    /**
     * response is not null and {@link Response#status status} is equal to ResponseStatuses#OK OK}
     *
     * @param response The response
     */
    void onResponse(Response response);

    /**
     * If {@link Response#status status} is anything but esponseStatuses#OK OK} then response wont be null.
     * If an exception was thrown by OkHttp then exception wont be null.
     *
     * @param response  @Nullable The response
     * @param exception @Nullable The exception
     */
    void onFailed(Response response, IOException exception);
}
