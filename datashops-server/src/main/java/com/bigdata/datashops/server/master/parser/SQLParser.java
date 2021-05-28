package com.bigdata.datashops.server.master.parser;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.enums.Macro;
import com.bigdata.datashops.server.utils.MacroUtil;

public class SQLParser {

    private static final String MacroPattern = "\\$\\{(YEAR|MONTH|DATE|HOUR|MINUTE|SECOND)(\\-|\\+)([^}]*)\\}";

    private static Pattern pattern = Pattern.compile(MacroPattern);

    private static final String PATTERN_STR = "\\$\\{(^dateformat|^timestamp|.*?)\\}";

    private static Pattern PATTERN = Pattern.compile(PATTERN_STR);

    public static String parseSQL(String sql) {
        return parseSQL(null, sql);
    }

    public static String parseSQLed(LocalDateTime baseTime, String sql) {
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

    public static String parseSQL(LocalDateTime baseTime, String sql) {
        LocalDateTime ldt = LocalDateTime.now();
        if (!Objects.isNull(baseTime)) {
            ldt = baseTime;
        }
        Matcher m = PATTERN.matcher(sql);
        while (m.find()) {
            String matchStr = m.group(1);
            boolean ts = false, df = false;
            if (matchStr.startsWith(Constants.MARCO_TYPE_TIMESTAMP)) {
                ts = true;
            }
            if (matchStr.startsWith(Constants.MARCO_TYPE_DATEFORMAT)) {
                df = true;
            }
            matchStr = matchStr.replace(Constants.MARCO_TYPE_DATEFORMAT, "").replace(Constants.MARCO_TYPE_TIMESTAMP, "")
                               .replace("(", "").replace(")", "");
            String[] arrs = matchStr.split(Constants.SEPARATOR_COMMA);
            String res;
            if (arrs.length == 1) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(arrs[0]);
                res = ldt.format(dateTimeFormatter);
            } else {
                DateTimeFormatter dateTimeFormatter = null;
                String offset;
                String unit;
                if (arrs.length == 3) {
                    dateTimeFormatter = DateTimeFormatter.ofPattern(arrs[0]);
                    offset = arrs[1];
                    unit = arrs[2];
                } else {
                    offset = arrs[0];
                    unit = arrs[1];
                }
                switch (Macro.valueOf(unit)) {
                    case YEAR:
                        ldt = ldt.plusYears(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                    case MONTH:
                        ldt = ldt.plusMonths(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                    case WEEK:
                        ldt = ldt.plusWeeks(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                    case DAY:
                        ldt = ldt.plusDays(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                    case HOUR:
                        ldt = ldt.plusHours(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                    case MINUTE:
                        ldt = ldt.plusMinutes(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                    case SECOND:
                        ldt = ldt.plusSeconds(Long.parseLong(org.apache.commons.lang3.StringUtils.trim(offset)));
                        break;
                }
                if (ts) {
                    res = String.valueOf(ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli());
                } else {
                    res = ldt.format(dateTimeFormatter);
                }
            }
            String st = "\\$\\{" + matchStr + "\\}";
            if (ts) {
                st = String.format("\\$\\{%s\\(%s\\)\\}", Constants.MARCO_TYPE_TIMESTAMP, matchStr);
            }
            if (df) {
                st = String.format("\\$\\{%s\\(%s\\)\\}", Constants.MARCO_TYPE_DATEFORMAT, matchStr);
            }
            sql = sql.replaceAll(st, res);
            ldt = baseTime == null ? LocalDateTime.now() : baseTime;
        }
        return sql;
    }

    public static void main(String[] args) {
        String sql = "select * from default.test where ${timestamp()}  dt='${dateformat(yyyyMMdd)}' and "
                             + "ctime='${dateformat" + "(yyyy-MM-dd HH:mm:ss,-2,DAY)}' and "
                             + "hour=${dateformat(HH,-1 ,HOUR)} and" + " utime=${timestamp(-2,DAY)}";
        System.out.println(parseSQL(sql));
    }
}
