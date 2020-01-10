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
    /**
     * 标准时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 只有日期的格式 yyyy-MM-dd
     */
    public static DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 格式化成标准时间格式  yyyy-MM-dd HH:mm:ss
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatStandardTime(Date date) {
        return convertFromDate(date).format(DATE_TIME);
    }

    /**
     * 格式化标准日期格式
     * @param date 日期
     * @return yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        return convertFromDate(date).format(DATE);
    }

    /**
     * 判断是否是今天
     * @param date 日期
     * @return boolean
     */
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
     * @return 日期
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
     * @return 日期
     */
    public static Date theEndOfDay(int days) {
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
        todayEnd = todayEnd.plusDays(days);
        return convertFromLocalDateTime(todayEnd);
    }

    /**
     * 时间类型转换
     * @param date 日期
     * @return jdk8 时间类型
     */
    public static LocalDateTime convertFromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 时间类型转换
     * @param localDateTime jdk8时间类型
     * @return 日期
     */
    public static Date convertFromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
