package util;

/**
 * @author erik.wang
 * @date 2020-07-01 21:25
 */
public class PrintThread {

    public static void printThreadName() {
        System.out.println(threadAbout());
    }

    public static String threadAbout() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        return String.format("thread=%s %s.%s:%d", Thread.currentThread().getName(), stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

}
