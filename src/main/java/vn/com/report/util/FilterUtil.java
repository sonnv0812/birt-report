package vn.com.report.util;

public class FilterUtil {
    public static String fileNameFilter (String str) {
        return str.replaceAll("[\\\\/%:*?\"<>|]", "");
    }
}
