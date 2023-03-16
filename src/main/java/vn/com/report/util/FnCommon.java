package vn.com.report.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FnCommon {

    private static final Logger LOGGER = LoggerFactory.getLogger(FnCommon.class);

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
