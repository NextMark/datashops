package com.bigdata.datashops.server.master.parser;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bigdata.datashops.server.utils.MacroUtil;

public class SQLParser {

    private static final String MacroPattern = "\\$\\{(YEAR|MONTH|DATE|HOUR|MINUTE|SECOND)(\\-|\\+)([^}]*)\\}";

    private static Pattern pattern = Pattern.compile(MacroPattern);

    public static String parseSQL(String sql) {
        return parseSQL(null, sql);
    }

    public static String parseSQL(LocalDateTime baseTime, String sql) {
        Assert.notNull(sql, "SQL is null");
        LocalDateTime ldt = LocalDateTime.now();
        if (baseTime != null) {
            ldt = baseTime;
        }
        Long now = ldt.toEpochSecond(ZoneOffset.of("+8"));
        if (sql.contains(MacroUtil.DATE)) {
            sql = sql.replace(MacroUtil.DATE, StringUtils.quote(MacroUtil.getDate(now)));
        }
        if (sql.contains(MacroUtil.YEAR)) {
            sql = sql.replace(MacroUtil.YEAR, StringUtils.quote(MacroUtil.getYear(now).toString()));
        }
        if (sql.contains(MacroUtil.MONTH)) {
            sql = sql.replace(MacroUtil.MONTH, StringUtils.quote(MacroUtil.getMonth(now).toString()));
        }
        if (sql.contains(MacroUtil.DAY)) {
            sql = sql.replace(MacroUtil.DAY, StringUtils.quote(MacroUtil.getDay(now).toString()));
        }
        if (sql.contains(MacroUtil.HOUR)) {
            sql = sql.replace(MacroUtil.HOUR, StringUtils.quote(MacroUtil.getHour(now)));
        }
        if (sql.contains(MacroUtil.MINUTE)) {
            sql = sql.replace(MacroUtil.MINUTE, StringUtils.quote(MacroUtil.getMinute(now).toString()));
        }
        if (sql.contains(MacroUtil.SECOND)) {
            sql = sql.replace(MacroUtil.SECOND, StringUtils.quote(MacroUtil.getSecond(now).toString()));
        }
        if (sql.contains(MacroUtil.YESTERDAY)) {
            sql = sql.replace(MacroUtil.YESTERDAY, StringUtils.quote(MacroUtil.getYesterday(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_WEEK_BEGIN)) {
            sql = sql.replace(MacroUtil.FORWARD_WEEK_BEGIN, StringUtils.quote(MacroUtil.getFirstDayOfForwardWeek(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_WEEK_END)) {
            sql = sql.replace(MacroUtil.FORWARD_WEEK_END, StringUtils.quote(MacroUtil.getLastDayOfForwardWeek(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_MONTH_BEGIN)) {
            sql = sql.replace(MacroUtil.FORWARD_MONTH_BEGIN,
                    StringUtils.quote(MacroUtil.getBeginDayOfForwardMonth(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_MONTH_END)) {
            sql = sql.replace(MacroUtil.FORWARD_MONTH_END, StringUtils.quote(MacroUtil.getLastDayOfForwardMonth(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_7_DAY)) {
            sql = sql.replace(MacroUtil.FORWARD_7_DAY, StringUtils.quote(MacroUtil.get7DaysAgo(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_30_DAY)) {
            sql = sql.replace(MacroUtil.FORWARD_30_DAY, StringUtils.quote(MacroUtil.get30DaysAgo(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_365_DAY)) {
            sql = sql.replace(MacroUtil.FORWARD_365_DAY, StringUtils.quote(MacroUtil.get365DaysAgo(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_2_MONTH_BEGIN)) {
            sql = sql.replace(MacroUtil.FORWARD_2_MONTH_BEGIN,
                    StringUtils.quote(MacroUtil.getBeginDayOfForward2Month(now)));
        }
        if (sql.contains(MacroUtil.FORWARD_2_MONTH_END)) {
            sql = sql.replace(MacroUtil.FORWARD_2_MONTH_END,
                    StringUtils.quote(MacroUtil.getLastDayOfForward2Month(now)));
        }
        Matcher m = pattern.matcher(sql);
        while (m.find()) {
            String exp = m.group();
            String macro = m.group(1);
            String plus = m.group(2);
            int offset = Integer.parseInt(m.group(3));
            if (plus.equals("-")) {
                offset *= -1;
            }

            ldt = MacroUtil.parseMacro(macro, offset, ldt);
            sql = sql.replace(exp, StringUtils.quote(MacroUtil.parseLdtToStr(macro, ldt)));
            ldt = baseTime == null ? LocalDateTime.now() : baseTime;
        }
        return sql;
    }
}
