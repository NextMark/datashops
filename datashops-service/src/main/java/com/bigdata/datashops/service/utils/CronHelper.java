package com.bigdata.datashops.service.utils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.compress.utils.Lists;
import org.quartz.CronExpression;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.LocalDateUtils;
import com.bigdata.datashops.model.dto.DtoCronExpression;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.exception.ValidationException;
import com.bigdata.datashops.model.pojo.quartz.Day;
import com.bigdata.datashops.model.pojo.quartz.Hour;
import com.bigdata.datashops.model.pojo.quartz.Minute;
import com.bigdata.datashops.model.pojo.quartz.Month;
import com.bigdata.datashops.model.pojo.quartz.Week;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

public class CronHelper {

    public static String buildCronExpression(DtoCronExpression dtoCronExpression) {
        String expression = "%s %s %s %s %s %s";
        switch (SchedulingPeriod.of(dtoCronExpression.getSchedulingPeriod())) {
            case MINUTE:
                Minute minute = JSONUtils.parseObject(dtoCronExpression.getConfig(), Minute.class);
                assert minute != null;
                expression = String.format(expression, "00", "*/" + minute.getPeriod(),
                        minute.getMinuteBegin() + "-" + minute.getMinuteEnd(), "*", "*", "?");
                break;
            case HOUR:
                Hour hour = JSONUtils.parseObject(dtoCronExpression.getConfig(), Hour.class);
                assert hour != null;
                int type = hour.getType();
                if (type == 2) {
                    expression = String.format(expression, "00", "00", hour.getHours(), "*", "*", "?");
                }
                if (type == 1) {
                    expression = String.format(expression, "00", hour.getHourMinute(),
                            String.format("%s-%s/%s", hour.getHourBegin(), hour.getHourEnd(), hour.getHourPeriod()),
                            "*", "*", "?");
                }
                break;
            case DAY:
                Day day = JSONUtils.parseObject(dtoCronExpression.getConfig(), Day.class);
                assert day != null;
                expression = String.format(expression, "00", day.getMinute(), day.getHour(), "*", "*", "?");
                break;
            case WEEK:
                Week week = JSONUtils.parseObject(dtoCronExpression.getConfig(), Week.class);
                assert week != null;
                expression =
                        String.format(expression, "00", week.getMinute(), week.getHour(), "?", "*", week.getWeeks());
                break;
            case MONTH:
                Month month = JSONUtils.parseObject(dtoCronExpression.getConfig(), Month.class);
                assert month != null;
                expression =
                        String.format(expression, "00", month.getMinute(), month.getHour(), month.getDays(), "*", "?");
                break;
            default:
                break;
        }
        boolean valid = CronExpression.isValidExpression(expression);
        if (!valid) {
            throw new ValidationException("Can not build cron expression, " + dtoCronExpression.toString());
        }
        return expression;
    }

    /**
     * 获取下一次执行时间
     */
    public static Date getNextTime(String cronExpr) {
        Cron cron = validCron(cronExpr);
        return getNextTime(cron);
    }

    /**
     * 获取下一次执行时间
     */
    public static Date getNextTime(Cron cron) {
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime nextTime = executionTime.nextExecution(ZonedDateTime.now()).orElse(null);
        if (Objects.isNull(nextTime)) {
            throw new RuntimeException("获取下一次执行时间失败");
        }
        return Date.from(nextTime.toInstant());
    }

    public static ZonedDateTime toZonedDateTime(Date utilDate) {
        if (utilDate == null) {
            return null;
        }
        final ZoneId systemDefault = ZoneId.systemDefault();
        return ZonedDateTime.ofInstant(utilDate.toInstant(), systemDefault);
    }

    public static Date getLastTime(String cronExpr, Date date) {
        Cron cron = validCron(cronExpr);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime lastTime = executionTime.lastExecution(toZonedDateTime(date)).orElse(null);
        if (Objects.isNull(lastTime)) {
            throw new RuntimeException("获取上一次执行时间失败");
        }
        return Date.from(lastTime.toInstant());
    }

    public static Date getOffsetTriggerTime(String cron, Date date, int offset) {
        CronExpression expression = null;
        try {
            expression = new CronExpression(cron);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = expression.getNextValidTimeAfter(date);
        for (int i = offset; i <= 0; i++) {
            date = getLastTime(cron, date);
        }
        return date;
    }

    /**
     * 获取上一次执行时间
     */
    public static Date getLastTime(String cronExpr) {
        Cron cron = validCron(cronExpr);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime lastTime = executionTime.lastExecution(ZonedDateTime.now()).orElse(null);
        if (Objects.isNull(lastTime)) {
            throw new RuntimeException("获取上一次执行时间失败");
        }
        return Date.from(lastTime.toInstant());
    }

    /**
     * 获取上一次执行时间
     */
    public static Date getLastTime(Cron cron) {
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime lastTime = executionTime.lastExecution(ZonedDateTime.now()).orElse(null);
        if (Objects.isNull(lastTime)) {
            throw new RuntimeException("获取上一次执行时间失败");
        }
        return Date.from(lastTime.toInstant());
    }

    /**
     * 验证并返回Cron
     */
    public static Cron validCron(String cronExpr) {
        try {
            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
            CronParser cronParser = new CronParser(cronDefinition);
            Cron cron = cronParser.parse(cronExpr);
            cron.validate();
            return cron;
        } catch (Exception e) {
            throw new RuntimeException("cron表达式验证失败");
        }
    }

    public static List<Date> getDependencyBizTime(Date date, int schedulingPeriod, int offset, String cron) {
        List<Date> result = Lists.newArrayList();
        LocalDateTime ldt = LocalDateUtils.dateToLocalDateTime(date);
        LocalDateTime bizTime;
        switch (SchedulingPeriod.of(schedulingPeriod)) {
            case MINUTE:
            case HOUR:
            case DAY:
                result.add(CronHelper.getOffsetTriggerTime(cron, date, offset));
                break;
            case WEEK:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.WEEKS);
                String[] weeks = cron.split(Constants.SEPARATOR_WHITE_SPACE)[5].split(Constants.SEPARATOR_COMMA);
                for (String week : weeks) {
                    result.add(LocalDateUtils.parseStringToDate(
                            LocalDateUtils.getDateOfWeekStr(bizTime, Integer.parseInt(week))));
                }
                break;
            case MONTH:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.MONTHS);
                String[] months = cron.split(Constants.SEPARATOR_WHITE_SPACE)[3].split(Constants.SEPARATOR_COMMA);
                for (String month : months) {
                    result.add(LocalDateUtils.parseStringToDate(
                            LocalDateUtils.getDateOfMonthStr(bizTime, Integer.parseInt(month))));
                }
                break;
            default:
                break;
        }
        return result;
    }
}
