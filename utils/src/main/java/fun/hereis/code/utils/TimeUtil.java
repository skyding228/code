package fun.hereis.code.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间相关工具类
 *
 * @author weichunhe
 * created at 2018/12/7
 */
public class TimeUtil {
    public static DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static long MILLS_PER_SECOND = 1000L;

    public static long MILLS_PER_MINUTE = 60 * MILLS_PER_SECOND;

    public static long MILLS_PER_HOUR = 60 * MILLS_PER_MINUTE;

    public static long MILLS_PER_DAY = 24 * MILLS_PER_HOUR;

    public static long THIRTY_DAYS = 30 * MILLS_PER_DAY;

    public static boolean isAnHourLater(Date date) {
        return date.getTime() - System.currentTimeMillis() > MILLS_PER_HOUR;
    }

    public static boolean is30DaysAgo(Date date) {
        if (date == null) {
            return false;
        }
        return System.currentTimeMillis() - date.getTime() > THIRTY_DAYS;
    }

    public static boolean isBeforeNow(Date date) {
        return System.currentTimeMillis() - date.getTime() >= 0;
    }

    public static boolean isAfterNow(Date date) {
        return System.currentTimeMillis() - date.getTime() <= 0;
    }

    public static String formatToTime(Date date) {
        return convertFromDate(date).format(TIME);
    }

    public static String formatToDay(Date date) {
        return convertFromDate(date).format(DAY);
    }

    public static Date minutesAfterNow(int minutes) {
        return new Date(System.currentTimeMillis() + minutes * MILLS_PER_MINUTE);
    }

    public static Date theEndOfToday() {
        return theEndOfDay(0);
    }

    public static Date theBeginningOfToday() {
        return theBeginningOfDay(0);
    }

    public static Date theEndOfYesterday() {
        return theEndOfDay(-1);
    }

    public static Date theBeginningOfYesterday() {
        return theBeginningOfDay(-1);
    }

    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        return convertFromDate(date).getDayOfYear() == LocalDate.now().getDayOfYear();
    }

    /**
     * 获取某一天的开始时间 00:00:00
     *
     * @param days 0 表示今天,正数表示以后，负数表示之前
     * @return
     */
    public static Date theBeginningOfDay(int days) {
        LocalDateTime todayBeginning = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        todayBeginning = todayBeginning.plusDays(days);
        return convertFromLocalDateTime(todayBeginning);
    }

    /**
     * 获取某一天的结束时间 23:59:59
     *
     * @param days 0 表示今天,正数表示以后，负数表示之前
     * @return
     */
    public static Date theEndOfDay(int days) {
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
        todayEnd = todayEnd.plusDays(days);
        return convertFromLocalDateTime(todayEnd);
    }


    public static LocalDateTime convertFromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date convertFromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
