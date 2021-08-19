package devx.app.licensee.common.services;


public class KeyConstants {


    public static String Result="result";
    public static String Message="message";

    public static String List="list";

    public static Integer StudentStatusNone = 0;
    public static Integer StudentStatusPresent = 1;
    public static Integer StudentStatusAbsent = 2;
    public static Integer StudentStatusSingleUnboard = 3;


    public static Integer TripUnBoardNone = 0;
    public static Integer TripUnBoardDone = 1;

    public static Integer TripStatusNone = 0;
    public static Integer TripStatusStart = 1;
    public static Integer TripStatusEnd = 2;

    public static boolean NavigationCallBln = true;
    public static Thread sendingThread;
    public static Thread socketThread;
    public static boolean isSocketConnected = false;
    public static boolean isGPSStartAnimReady=true;
    public static boolean isGPSStopAnimReady=true;


}
