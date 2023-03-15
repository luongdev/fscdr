package com.metechvn.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utils {
    private Utils() {
    }

    public static String camelCaseToUnderscore(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append("_");
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat(pattern).format(date);
    }

}
