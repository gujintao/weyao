package com.weyao.srv.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final SimpleDateFormat 年月日 = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat 年月日时分秒 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseDate(String dateStr, DateFormat dateFormat) {

        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getCurrentDate() {

        /*try {
            return 年月日.parse(年月日.format(new Date()));
        } catch (ParseException e) {
            return null;
        }*/
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static long diffDay(Date fromTime, Date toTime) {

        return (toTime.getTime() - fromTime.getTime()) / 86400000;
    }

    public static void main(String[] args) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        System.out.println("calendar.getTime() = " + calendar.getTime());
    }
}
