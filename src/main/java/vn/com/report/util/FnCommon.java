package vn.com.report.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class FnCommon {

    private static final Logger LOGGER = LoggerFactory.getLogger(FnCommon.class);
    private static final ResourceBundle RESOURCE_BUNDLE = getResource();

    private static ResourceBundle getResource() {
        try {
            ResourceBundle appConfigRB = ResourceBundle.getBundle(
                    Constant.FILE_MESS);
            return appConfigRB;
        } catch (Exception e) {
            LOGGER.error("Loi! getResourceBundle: " + e.getMessage());
        }
        return null;
    }

    /**
     * Convert date date to string date
     *
     * @param date
     * @param isFullDateTime:true: full date time, false: date sort
     * @return
     */
    public static String convertDateToString(Date date, Boolean isFullDateTime) {
        String strDate;
        if (date == null) {
            return "";
        }
        if (isFullDateTime) {
            strDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
        } else {
            strDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
        }
        return strDate;
    }

    /**
     * Go bo dau tieng viet
     *
     * @param s
     * @return
     */
    public static String removeAccent(String s) {
        if (s == null) {
            return "";
        }
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace("đ", "d").replace("Đ", "D");
    }

    /**
     * doc file properties trong cau hinh thu muc default
     *
     * @param key
     * @return
     */
    public static String getValueFileMess(String key) {
        String value = RESOURCE_BUNDLE.containsKey(key)
                ? RESOURCE_BUNDLE.getString(key)
                : Constant.STR_EMTY;
        if (value.trim().length() <= 0) {
            LOGGER.error("Not value with key:" + key + ", in file properties");
        }
        return value;
    }

    public static String convertObjectToStringJson(Object object) {
        String strMess = "";

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            strMess = gson.toJson(object);
        } catch (Exception var4) {
            LOGGER.error("Loi! FnCommon.convertObjectToStringJson", var4);
        }

        return strMess;
    }
}
