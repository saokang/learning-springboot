package com.example.util;


import org.apache.xmlbeans.impl.regex.REUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class DateUtils {

    public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd";
    public static final String STANDARD_TIME_PATTERN = "HH:mm:ss";
    public static final String STANDARD_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy/MM/dd";
    public static final String DATE_TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String TIME_ZONE_NEW_YORK = "America/New_York"; // 美国东部时间（纽约）
    public static final String TIME_ZONE_LOS_ANGELES = "America/Los_Angeles"; // 美国西部时间（洛杉矶）
    public static final String TIME_ZONE_LONDON = "Europe/London"; // 英国格林威治标准时间
    public static final String TIME_ZONE_PARIS = "Europe/Paris"; // 法国中欧时间
    public static final String TIME_ZONE_TOKYO = "Asia/Tokyo"; // 日本标准时间
    public static final String TIME_ZONE_SHANGHAI = "Asia/Shanghai"; // 中国标准时间
    public static final String TIME_ZONE_SYDNEY = "Australia/Sydney"; // 澳大利亚东部时间


    public static Date newDate() {
        return new Date();
    }

    public static long newTimestamp() {
        return System.currentTimeMillis();
    }

    // 使用 SimpleDateFormat 格式化 Date
    public static String format(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    // 使用 DateTimeFormatter 格式化 LocalDateTime
    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    // 使用 SimpleDateFormat 解析字符串为 Date
    public static Date parse2Date(String dateStr, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    // 使用 DateTimeFormatter 解析字符串为 LocalDateTime
    public static LocalDateTime parse2LocalDateTime(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, formatter);
    }

    // 根据年份和周数获取该周的开始日期（周一）
    public static LocalDate getStartDateOfWeek(int year, int week) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfWeekBasedYear(), week)
                .with(weekFields.dayOfWeek(), 1); // 1 表示周一
    }

    // 根据年份和周数获取该周的结束日期（周日）
    public static LocalDate getEndDateOfWeek(int year, int week) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfWeekBasedYear(), week)
                .with(weekFields.dayOfWeek(), 7); // 7 表示周日
    }

    // 对 Date 对象的秒进行加减法运算
    public static Date adjustSeconds(Date date, long seconds) {
        Instant instant = date.toInstant().plusSeconds(seconds);
        return Date.from(instant);
    }

    // 对 Date 对象的分钟进行加减法运算
    public static Date adjustMinutes(Date date, long minutes) {
        Instant instant = date.toInstant().plusSeconds(minutes * 60);
        return Date.from(instant);
    }

    // 对 Date 对象的小时进行加减法运算
    public static Date adjustHours(Date date, long hours) {
        Instant instant = date.toInstant().plusSeconds(hours * 3600);
        return Date.from(instant);
    }

    // 对 Date 对象的天进行加减法运算
    public static Date adjustDays(Date date, long days) {
        Instant instant = date.toInstant().plusSeconds(days * 86400);
        return Date.from(instant);
    }

    // 对 Date 对象的周进行加减法运算
    public static Date adjustWeeks(Date date, long weeks) {
        Instant instant = date.toInstant().plusSeconds(weeks * 604800);
        return Date.from(instant);
    }

    // 对 Date 对象的月进行加减法运算
    public static Date adjustMonths(Date date, long months) {
        Instant instant = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(months).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    // 对 Date 对象的年进行加减法运算
    public static Date adjustYears(Date date, long years) {
        Instant instant = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusYears(years).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    // 对 LocalDateTime 对象的秒进行加减法运算
    public static LocalDateTime adjustSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime.plusSeconds(seconds);
    }

    // 对 LocalDateTime 对象的分钟进行加减法运算
    public static LocalDateTime adjustMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    // 对 LocalDateTime 对象的小时进行加减法运算
    public static LocalDateTime adjustHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    // 对 LocalDateTime 对象的天进行加减法运算
    public static LocalDateTime adjustDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    // 对 LocalDate 对象的周进行加减法运算
    public static LocalDate adjustWeeks(LocalDate date, long weeks) {
        return date.plusWeeks(weeks);
    }

    // 对 LocalDate 对象的月进行加减法运算
    public static LocalDate adjustMonths(LocalDate date, long months) {
        return date.plusMonths(months);
    }

    // 对 LocalDate 对象的年进行加减法运算
    public static LocalDate adjustYears(LocalDate date, long years) {
        return date.plusYears(years);
    }

    // 将 LocalDateTime 转换为 Date
    public static Date convertToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    // 将 Date 转换为 LocalDateTime
    public static LocalDate convertToLocalDate(Date date) {
        return LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    // 将 Date 转换为 LocalDateTime
    public static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // 获取当前时间并按照指定格式进行格式化
    public static String getCurrentTimeFormatted(String format) {
        // 创建一个 Date 实例，表示当前时间
        Date currentTime = new Date();
        // 创建一个 SimpleDateFormat 实例来定义日期格式
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        // 使用 SimpleDateFormat 格式化当前时间，返回格式化后的当前时间
        return formatter.format(currentTime);
    }


    // 获取日期的星期，星期一为1，星期二为2，以此类推
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek + 5) % 7 + 1;
    }

    // 获取给定日期是星期几
    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek();
    }

    // 使用 Date 获取月份中的第几天
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // 使用 LocalDate 获取月份中的第几天
    public static int getDayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    // 使用 Date 获取某个月份的最大天数
    public static int getMaxDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // 使用 Date 获取某个月份的最大天数的日期
    public static Date getMaxDayOfMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    // 使用 LocalDate 获取某个月份的最大天数
    public static int getMaxDayOfMonth(LocalDate date) {
        return date.lengthOfMonth();
    }

    // 使用 Date 获取某个月份的第一天
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    // 使用 LocalDate 获取某个月份的第一天
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    // 使用 Date 获取某个日期在年份中的第几天
    public static int getDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    // 使用 LocalDate 获取某个日期在年份中的第几天
    public static int getDayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }

    // 判断两个日期是否相等
    public static boolean datesAreEqual(Date date1, Date date2) {
        return date1.equals(date2);
    }

    // 比较两个日期的顺序（哪一个在前）
    public static int compareDates(Date date1, Date date2) {
        return date1.compareTo(date2);
    }

    /**
     * 判断一个字符串是否是时间格式 hh:mm:ss
     * String time1 = "12:34:56"; // 符合时间格式
     * String time2 = "25:34:56"; // 不符合时间格式，小时超出范围
     * String time3 = "12:67:56"; // 不符合时间格式，分钟超出范围
     * String time4 = "12:34:99"; // 不符合时间格式，秒超出范围
     * String time5 = "12:34";    // 不符合时间格式，缺少秒
     *
     * @param timeStr string
     * @return true | false
     */
    public static boolean isValidTimeFormat(String timeStr) {
        // 定义时间格式的正则表达式
        String timeRegex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
        // 使用 Pattern 类进行匹配
        return Pattern.matches(timeRegex, timeStr);
    }

    // 判断一个字符串是否是日期格式 yyyy-MM-dd
    public static boolean isDateFormat(String dateStr) {
        // 定义日期格式的正则表达式
        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        // 使用 Pattern 类进行匹配
        return Pattern.matches(dateRegex, dateStr);
    }

    // 判断日期时间字符串是否是合法的格式，并且时间是否存在
    public static boolean isValidDateTime(String dateTimeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            // 手动检查月份和日期是否合法
            int year = dateTime.getYear();
            int month = dateTime.getMonthValue();
            int day = dateTime.getDayOfMonth();
            int hour = dateTime.getHour();
            int minute = dateTime.getMinute();
            int second = dateTime.getSecond();
            // if (month < 1 || month > 12 || day < 1 || day > LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1).minusDays(1).getDayOfMonth()) return false; // 月份或日期不合法
            String date = dateTimeString.split(" ")[0];
            String time = dateTimeString.split(" ")[1];
            String y4 = date.split("-")[0];
            String MM = date.split("-")[1];
            String dd = date.split("-")[2];
            String HH = time.split(":")[0];
            String mm = time.split(":")[1];
            String ss = time.split(":")[2];
            if (Integer.parseInt(y4) != year || Integer.parseInt(MM) != month || Integer.parseInt(dd) != day ||
                    Integer.parseInt(HH) != hour || Integer.parseInt(mm) != minute || Integer.parseInt(ss) != second) {
                return false;
            }
            return true; // 格式合法且时间存在
        } catch (DateTimeException e) {
            return false; // 格式不合法或时间不存在
        }
    }

    // 判断一个 Date 对象是否是周末
    public static boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    // 判断一个 LocalDate 对象是否是周末
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    // 使用 Date 类实现时间转换
    public static String getTimeAgo(Date publishedTime, boolean useChinese) {
        long currentTimeMillis = System.currentTimeMillis();
        long publishedTimeMillis = publishedTime.getTime();
        long diffSeconds = (currentTimeMillis - publishedTimeMillis) / 1000;

        if (diffSeconds < 60) {
            return useChinese ? "刚刚" : "Just now";
        } else if (diffSeconds < 3600) {
            long diffMinutes = diffSeconds / 60;
            return (useChinese ? diffMinutes + "分钟前" : diffMinutes + " minutes ago");
        } else if (diffSeconds < 86400) {
            long diffHours = diffSeconds / 3600;
            return (useChinese ? diffHours + "小时前" : diffHours + " hours ago");
        } else if (diffSeconds < 2592000) {
            long diffDays = diffSeconds / 86400;
            return (useChinese ? diffDays + "天前" : diffDays + " days ago");
        } else if (diffSeconds < 31536000) {
            long diffMonths = diffSeconds / 2592000;
            return (useChinese ? diffMonths + "个月前" : diffMonths + " months ago");
        } else {
            long diffYears = diffSeconds / 31536000;
            return (useChinese ? diffYears + "年前" : diffYears + " years ago");
        }
    }

    // 使用 LocalDate 类实现时间转换
    public static String getTimeAgo(LocalDate publishedDate, boolean useChinese) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(publishedDate, currentDate);

        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        if (years > 0) {
            return (useChinese ? years + "年前" : years + " years ago");
        } else if (months > 0) {
            return (useChinese ? months + "个月前" : months + " months ago");
        } else if (days > 0) {
            return (useChinese ? days + "天前" : days + " days ago");
        } else {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime publishedDateTime = currentDateTime.minusDays(days);
            long diffSeconds = currentDateTime.toEpochSecond(ZoneOffset.from(currentDateTime)) - publishedDateTime.toEpochSecond(ZoneOffset.from(publishedDateTime));
            long diffMinutes = diffSeconds / 60;
            if (diffMinutes < 60) {
                return (useChinese ? diffMinutes + "分钟前" : diffMinutes + " minutes ago");
            } else {
                long diffHours = diffMinutes / 60;
                return (useChinese ? diffHours + "小时前" : diffHours + " hours ago");
            }
        }
    }

    // 使用 Date 类实现获取两个时间相差的天数
    public static long getDaysDifference(Date date1, Date date2) {
        long milliseconds1 = date1.getTime();
        long milliseconds2 = date2.getTime();
        long diffMilliseconds = milliseconds2 - milliseconds1;
        return diffMilliseconds / (24 * 60 * 60 * 1000);
    }

    // 使用 LocalDate 类实现获取两个时间相差的天数
    public static long getDaysDifference(LocalDate date1, LocalDate date2) {
        return ChronoUnit.DAYS.between(date1, date2);
    }

    // 使用 Date 类实现获取两个时间相差的秒数
    public static long getSecondsDifference(Date date1, Date date2) {
        long milliseconds1 = date1.getTime();
        long milliseconds2 = date2.getTime();
        return (milliseconds2 - milliseconds1) / 1000;
    }

    // 使用 LocalDate 类实现获取两个时间相差的秒数
    public static long getSecondsDifference(LocalDate date1, LocalDate date2) {
        LocalDateTime dateTime1 = date1.atStartOfDay();
        LocalDateTime dateTime2 = date2.atStartOfDay();
        Duration duration = Duration.between(dateTime1, dateTime2);
        return duration.getSeconds();
    }

    // 使用 Date 类实现计算两个时间相差的天数、小时数、分钟数和秒数
    public static String getTimeDifference(Date date1, Date date2) {
        long milliseconds1 = date1.getTime();
        long milliseconds2 = date2.getTime();
        long diffMilliseconds = milliseconds2 - milliseconds1;

        long diffSeconds = diffMilliseconds / 1000;
        long seconds = diffSeconds % 60;

        long diffMinutes = diffSeconds / 60;
        long minutes = diffMinutes % 60;

        long diffHours = diffMinutes / 60;
        long hours = diffHours % 24;

        long diffDays = diffHours / 24;

        return diffDays + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }

    // 使用 LocalDate 类实现计算两个时间相差的天数、小时数、分钟数和秒数
    public static String getTimeDifference(LocalDate date1, LocalDate date2) {
        LocalDateTime dateTime1 = date1.atStartOfDay();
        LocalDateTime dateTime2 = date2.atStartOfDay();
        Duration duration = Duration.between(dateTime1, dateTime2);

        long diffSeconds = duration.getSeconds();
        long seconds = diffSeconds % 60;

        long diffMinutes = diffSeconds / 60;
        long minutes = diffMinutes % 60;

        long diffHours = diffMinutes / 60;
        long hours = diffHours % 24;

        long diffDays = diffHours / 24;

        return diffDays + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }

    // 使用 Date 类实现获取两个日期之间的所有日期字符串列表
    public static List<String> getDatesBetween(Date startDate, Date endDate) {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            dateList.add(calendar.getTime().toString());
            calendar.add(Calendar.DATE, 1);
        }

        return dateList;
    }

    // 使用 LocalDate 类实现获取两个日期之间的所有日期字符串列表
    public static List<String> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<String> dateList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate.toString());
            currentDate = currentDate.plusDays(1);
        }

        return dateList;
    }

    // 将时间戳转换为指定时区的时间
    public static ZonedDateTime convertTimestampToTimeZone(long timestamp, String targetTimeZone) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZoneId zoneId = ZoneId.of(targetTimeZone);
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    // 将特定时区的时间解析为时间戳
    public static long parseTimeZoneToTimestamp(String dateTimeString, String timeZone) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(timeZone));
        return zonedDateTime.toEpochSecond();
    }

    // 根据周数获取开始日期和结束日期（使用 Date）
    public static Date[] getWeekDates(int weekOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 6); // 6天后就是周日
        Date endDate = calendar.getTime();
        return new Date[]{startDate, endDate};
    }

    // 根据周数获取开始日期和结束日期（使用 LocalDate） 0本周，-1上周，-2上上周，1下周，2下下周 返回date[0]开始日期、date[1]结束日期
    public static LocalDate[] getWeekDatesByLocalDate(int weekOffset) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusWeeks(weekOffset).with(DayOfWeek.MONDAY);
        LocalDate endDate = today.plusWeeks(weekOffset).with(DayOfWeek.SUNDAY);
        return new LocalDate[]{startDate, endDate};
    }

    // 生成随机时间（使用 Date）
    public static Date generateRandomTime() {
        long minDay = 0L; // 从1970年1月1日开始
        long maxDay = System.currentTimeMillis(); // 当前时间的毫秒数
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return new Date(randomDay);
    }

    public static void main(String[] args) {
        String justDateStr = "2024-06-10 15:02:44";

        Date date = newDate();
        System.out.println(format(date, STANDARD_DATE_TIME_PATTERN));

        Date tmpDate = parse2Date(justDateStr, STANDARD_DATE_TIME_PATTERN);
        System.out.println(format(tmpDate, DATE_TIME_PATTERN));

        tmpDate = adjustSeconds(tmpDate, 1);
        System.out.println(format(tmpDate, DATE_TIME_PATTERN));

        tmpDate = adjustWeeks(tmpDate, 1);
        System.out.println(format(tmpDate, DATE_TIME_PATTERN));

        tmpDate = adjustMonths(tmpDate, 1);
        System.out.println(format(tmpDate, DATE_TIME_PATTERN));

        System.out.println(getCurrentTimeFormatted(STANDARD_DATE_TIME_PATTERN));
        System.out.println(getCurrentTimeFormatted(STANDARD_DATE_PATTERN));
        System.out.println(getCurrentTimeFormatted(STANDARD_TIME_PATTERN));
        System.out.println(getCurrentTimeFormatted(DATE_TIME_PATTERN));
        System.out.println(getCurrentTimeFormatted(DATE_PATTERN));

        System.out.println(getDayOfWeek(date));
        LocalDate localDate = convertToLocalDate(date);
        System.out.println(getDayOfWeek(localDate));
        System.out.println(getDayOfMonth(date));
        System.out.println(getDayOfYear(date));


        System.out.println(getFirstDayOfMonth(date));
        System.out.println(getMaxDayOfMonth(date));
        System.out.println(getMaxDayOfMonthDate(date));

        System.out.println(compareDates(parse2Date(justDateStr, STANDARD_DATE_TIME_PATTERN), date));

        System.out.println(isValidDateTime(justDateStr));

        String tmpDateStr = "2024-12-31 15:02:44";
        System.out.println(isValidDateTime(tmpDateStr));

        System.out.println(isWeekend(tmpDate));

        justDateStr = "2024-06-10 15:02:44";
        System.out.println(getTimeAgo(parse2Date(justDateStr, STANDARD_DATE_TIME_PATTERN), true));


        System.out.println(Arrays.toString(getWeekDates(0)));
        System.out.println(Arrays.toString(getWeekDatesByLocalDate(0)));

    }

}
