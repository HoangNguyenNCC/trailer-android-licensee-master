package devx.app.licensee.webapi;

/**
 * Created by Aarna Systems Pvt.Ltd. on 25/5/16.
 * Developer : Sumit Untwal
 */

public class APIError {
    private final static String TAG = "APIError";
    private int statusCode;
    private String message;

    public APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
