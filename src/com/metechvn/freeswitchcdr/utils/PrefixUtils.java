package com.metechvn.freeswitchcdr.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrefixUtils {

    private PrefixUtils() {
    }

    public static String formatCollectionPrefix(long millis) {
        if (millis <= 0) return "197001";

        return new SimpleDateFormat("yyyyMM").format(new Date(millis));
    }

}
