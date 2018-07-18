package org.blogsite.youngsoft.piggybank.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by klee on 2018-01-29.
 */

public class TimeUtils {
    private final long time;
    private final Calendar calendar;

    public TimeUtils(final long time){
        this.time = time;
        this.calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
    }

    public static String getRandom(){
        long t = System.nanoTime();
        return String.valueOf(t);
    }

    public String getFormattedTime(String format){
        //yy.MM.dd.EEEE
        DateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(time));
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR);
    }

    public int getMinutes() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return calendar.get(Calendar.SECOND);
    }

    public int getDateCode(){
        StringBuilder sb = new StringBuilder();
        String year = String.valueOf(getYear()-2000);

        sb.append(String.valueOf(year)).append(String.valueOf(getMonth())).append(String.valueOf(getDay()))
                .append(String.valueOf(getHour())).append(String.valueOf(getMinutes()));
        return Integer.parseInt(sb.toString());
    }
}
