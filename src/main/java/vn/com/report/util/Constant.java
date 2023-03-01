package vn.com.report.util;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constant {

    protected static final Map<String, Locale> mapLocale = new ConcurrentHashMap<>();

    static {
        Locale vnLocal = new Locale("vi", "VN");
        Locale enLocal = new Locale("en", "US");
        mapLocale.put("vi", vnLocal);
        mapLocale.put("en", enLocal);
    }

    public static String STR_EMTY = "";
    public static String FILE_MESS = "message";
    public static final String REQUEST_MAPPING_V1 = "/api/v1";

}

