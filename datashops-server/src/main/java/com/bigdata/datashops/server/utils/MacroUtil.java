package com.bigdata.datashops.server.utils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import org.springframework.util.Assert;

import com.bigdata.datashops.model.enums.Macro;

/**
 * Created by qinshiwei on 2018/6/12.
 */
public class MacroUtil {
    public static final String MACRO_PREFIX = "$";

    public static final String YEAR = MACRO_PREFIX + "{YEAR}";

    public static final String MONTH = MACRO_PREFIX + "{MONTH}";

    public static final String DAY = MACRO_PREFIX + "{DAY}";

    public static final String HOUR = MACRO_PREFIX + "{HOUR}";

    public static final String MINUTE = MACRO_PREFIX + "{MINUTE}";

    public static final String SECOND = MACRO_PREFIX + "{SECOND}";

    public static final String DATE = MACRO_PREFIX + "{DATE}";

    public static final String YESTERDAY = MACRO_PREFIX + "{YESTERDAY}";

    public static final String FORWARD_MONTH_BEGIN = MACRO_PREFIX + "{FORWARD_MONTH_BEGIN}";

    public static final String FORWARD_MONTH_END = MACRO_PREFIX + "{FORWARD_MONTH_END}";

    public static final String FORWARD_WEEK_BEGIN = MACRO_PREFIX + "{FORWARD_WEEK_BEGIN}";

    public static final String FORWARD_WEEK_END = MACRO_PREFIX + "{FORWARD_WEEK_END}";

    public static final String FORWARD_7_DAY = MACRO_PREFIX + "{FORWARD_7_DAY}";

    public static final String FORWARD_365_DAY = MACRO_PREFIX + "{FORWARD_365_DAY}";

    public static final String FORWARD_30_DAY = MACRO_PREFIX + "{FORWARD_30_DAY}";

    public static final String FORWARD_2_MONTH_BEGIN = MACRO_PREFIX + "{FORWARD_2_MONTH_BEGIN}";

    public static final String FORWARD_2_MONTH_END = MACRO_PREFIX + "{FORWARD_2_MONTH_END}";

    private static final DateTimeFormatter DATE_FORMAT_SECOND = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getLocalDateTime() {
        return LocalDateTime.now().format(DATE_FORMAT_SECOND);
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter LDT = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private static final DateTimeFormatter DATE_FORMAT_HOUR = DateTimeFormatter.ofPattern("HH");

    public static Integer getYear(Long baseTime) {
        return getLocalDateTime(baseTime).getYear();
    }

    public static Integer getMonth(Long baseTime) {
        return getLocalDateTime(baseTime).getMonthValue();
    }

    public static Integer getDay(Long baseTime) {
        return getLocalDateTime(baseTime).getDayOfMonth();
    }

    public static String getHour(Long baseTime) {
        return getLocalDateTime(baseTime).format(DATE_FORMAT_HOUR);
    }

    public static Integer getMinute(Long baseTime) {
        return getLocalDateTime(baseTime).getMinute();
    }

    public static Integer getSecond(Long baseTime) {
        return getLocalDateTime(baseTime).getSecond();
    }

    public static String getDate(Long baseTime) {
        return getLocalDate(baseTime).format(DATE_FORMAT);
    }

    public static String getYesterday(Long baseTime) {
        return getLocalDate(baseTime).minusDays(1).format(DATE_FORMAT);
    }

    public static String get7DaysAgo(Long baseTime) {
        return getLocalDate(baseTime).minusDays(7).format(DATE_FORMAT);
    }

    public static String get30DaysAgo(Long baseTime) {
        return getLocalDate(baseTime).minusDays(30).format(DATE_FORMAT);
    }

    public static String get365DaysAgo(Long baseTime) {
        return getLocalDate(baseTime).minusDays(365).format(DATE_FORMAT);
    }

    public static String getFirstDayOfForwardWeek(Long baseTime) {
        LocalDate base = getLocalDate(baseTime);
        return base.minusDays(7).with(DayOfWeek.MONDAY).format(DATE_FORMAT);
    }

    public static String getLastDayOfForwardWeek(Long baseTime) {
        LocalDate base = getLocalDate(baseTime);
        return base.minusDays(7).with(DayOfWeek.SUNDAY).format(DATE_FORMAT);
    }

    public static String getLastDayOfForwardMonth(Long baseTime) {
        LocalDate base = getLocalDate(baseTime);
        base = base.minusMonths(1);
        return base.with(TemporalAdjusters.lastDayOfMonth()).format(DATE_FORMAT);
    }

    public static String getBeginDayOfForwardMonth(Long baseTime) {
        LocalDate base = getLocalDate(baseTime);
        base = base.minusMonths(1);
        return base.with(TemporalAdjusters.firstDayOfMonth()).format(DATE_FORMAT);
    }

    public static String getLastDayOfForward2Month(Long baseTime) {
        LocalDate base = getLocalDate(baseTime);
        base = base.minusMonths(2);
        return base.with(TemporalAdjusters.lastDayOfMonth()).format(DATE_FORMAT);
    }

    public static String getBeginDayOfForward2Month(Long baseTime) {
        LocalDate base = getLocalDate(baseTime);
        base = base.minusMonths(2);
        return base.with(TemporalAdjusters.firstDayOfMonth()).format(DATE_FORMAT);
    }

    public static LocalDateTime getLocalDateTime(Long baseTime) {
        Assert.notNull(baseTime, "Base time is null");
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(baseTime), ZoneId.systemDefault());
    }

    public static LocalDate getLocalDate(Long baseTime) {
        return getLocalDateTime(baseTime).toLocalDate();
    }

    public static String getDefaultBaseTime() {
        LocalDate now = LocalDate.now();
        return now.format(DATE_FORMAT);
    }

    public static String getDefaultBaseTime(int shift, String btt) {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusDays(shift);
        return now.format(DATE_FORMAT);
    }

    public static String getYesterdayBaseTime() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return yesterday.format(DATE_FORMAT);
    }

    public static LocalDateTime parseMacro(String macro, int offset, LocalDateTime ldt) {
        switch (Macro.valueOf(macro)) {
            case YEAR:
                ldt = ldt.plusYears(offset);
                break;
            case MONTH:
                ldt = ldt.plusMonths(offset);
                break;
            case DATE:
                ldt = ldt.plusDays(offset);
                break;
            case HOUR:
                ldt = ldt.plusHours(offset);
                break;
            case MINUTE:
                ldt = ldt.plusMinutes(offset);
                break;
            case SECOND:
                ldt = ldt.plusSeconds(offset);
                break;
        }
        return ldt;
    }

    public static String parseLdtToStr(String macro, LocalDateTime ldt) {
        String value = "";
        switch (Macro.valueOf(macro)) {
            case YEAR:
                value = MacroUtil.getYear(ldt.toEpochSecond(ZoneOffset.of("+8"))).toString();
                break;
            case MONTH:
                value = MacroUtil.getMonth(ldt.toEpochSecond(ZoneOffset.of("+8"))).toString();
                break;
            case DATE:
                value = MacroUtil.getDate(ldt.toEpochSecond(ZoneOffset.of("+8")));
                break;
            case HOUR:
                value = MacroUtil.getHour(ldt.toEpochSecond(ZoneOffset.of("+8")));
                break;
            case MINUTE:
                value = MacroUtil.getMinute(ldt.toEpochSecond(ZoneOffset.of("+8"))).toString();
                break;
            case SECOND:
                value = MacroUtil.getSecond(ldt.toEpochSecond(ZoneOffset.of("+8"))).toString();
                break;
        }
        return value;

    }
}
