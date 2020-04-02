package com.wafwaf.wafwaf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UnixTime {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());

    /**
     * return string with date format yyyy/MM/dd HH:mm:ss
     * */
    public static String toDate(long unixTime){
        return dateFormat.format(new Date(unixTime * 1000));
    }

    public static String toUnix(String date){

        try{
            Date d = dateFormat.parse(date);
            return String.valueOf(d.getTime()/1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static int getCurrentUnixTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public static int getUnixTimeDaysAgo(int daysCountAgo) {
        return (int) (System.currentTimeMillis() / 1000L) - 60 * 60 * 24 * daysCountAgo;
    }
}
