package com.base.utils.time;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * @description: LocalDateTime时间工具类
 * @author: yanwen
 * @create: 2022-04-01 11:18
 **/
@Slf4j
public class LocalDateTimeUtils {
    private static final String T = "T";

    private static final ZoneId ZONE_ID = ZoneOffset.systemDefault();
    /**
     * 8小时的秒数
     */
    private static final int OFFSET = 8 * 60 * 60;


    /**
     * LocalDateTime -> 秒
     *
     * @param localDateTime localDateTime
     * @return 秒
     */
    public static long toSeconds(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZONE_ID).toEpochSecond();
    }

    /**
     * LocalDateTime -> 毫秒
     *  竟然加了8小时
     *
     * @param localDateTime localDateTime
     * @return 毫秒
     */
    public static long toMilliSecond(LocalDateTime localDateTime) {
        // 比标准实际慢8小时，就是当前的时间了。
        return localDateTime.toInstant(ZoneOffset.ofTotalSeconds(OFFSET)).toEpochMilli();
    }

    /**
     * 秒级时间戳转LocalDateTime
     * @param time
     * @return
     */
    public static LocalDateTime secondToLocalDateTime(Long time){
        TimeZone aDefault = TimeZone.getDefault();
        // 毫秒级offset
        int rawOffset = aDefault.getRawOffset();
        String offset = "+" + rawOffset/1000/60/60;
        /*
         * 第一个参数 表示自1970-01-01T00：00：00Z以来的秒数
         * 第二个参数 表示纳秒数从0到999,999,999的秒数。
         * 第三个参数 代表区域偏移
         */
        LocalDateTime modifiedTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.of(offset));
        return modifiedTime;
    }

    /**
     * String类型yyyy-MM-dd HH:mm:ss格式转yyyy-MM-dd HH:mm:ss格式
     * @param time
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String time){
        if (time.contains(T)){
            time = time.replaceAll("T"," ");
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(time,df);
        return ldt;
    }

    /**
     * 带时区的时间字符串转 yyyy-MM-dd'T'HH:mm:ss 格式的LocalDateTime
     * @param time 带时区的时间字符串
     * @return
     */
    public static LocalDateTime timeZoneStrToLocalDateTime(String time){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = null;
        try {
            Date date = df.parse(time);
            localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            log.info(e.getMessage(),e);
        }
        return localDateTime;
    }

    /**
     * 判断当前时间是否在给定的时间范围内 - 闭区间
     * @param currentTime 当前时间
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return
     */
    public boolean inRange(LocalDateTime currentTime,LocalDateTime startTime,LocalDateTime endTime){
        return (currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) <= 0);
    }

    /**
     * 判断当前时间是否在给定的时间范围内 - 区间界限可控
     * @param currentTime
     * @param startTime
     * @param endTime
     * @param left
     * @param right
     * @return
     */
    public boolean inRange(LocalDateTime currentTime,LocalDateTime startTime,LocalDateTime endTime,boolean left,boolean right){
        // 闭区间
        if (left && right){
            return this.inRange(currentTime,startTime,endTime);
        }
        // 左闭区间
        if (left){
            return  (currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) < 0);
        }
        if (right){
            return (currentTime.compareTo(startTime) > 0 && currentTime.compareTo(endTime) <= 0);
        }
        return false;
    }



    /**
     * LocalDateTime转String
     * @param time LocalDateTime时间
     * @param excludeT 是否排除T
     * @return
     */
    public static String localDateTimeToStr(LocalDateTime time,boolean excludeT){
        String str = time.toString();
        if (excludeT){
            return str.replace("T"," ");
        }
        return str;
    }
}
