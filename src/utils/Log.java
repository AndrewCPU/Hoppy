package utils;

/**
 * Created by stein on 5/14/2017.
 */
public class Log {
    public static void d(String m){
        l("DEBUG",m);
    }
    public static void e(String m){
        l("ERROR",m);
    }
    public static void i(String m){
        l("INFO",m);
    }
    public static void l(String h, String m){
        System.out.println("[" + h + "] " + m);
    }
}
