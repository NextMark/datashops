package com.bigdata.datashops.service.utils;

import java.text.ParseException;
import java.util.Date;

public class JobHelper {
    public static Date getBizDate(String cron) {
        return CronHelper.getLastTime(cron);
    }

    public static void main(String[] args) throws ParseException {
        //        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        //
        //        Date nextTime = new Date();
        //        CronExpression expression;
        //        expression = new CronExpression("00 */5 04-23 * * ?");
        //        nextTime = expression.getNextValidTimeAfter(nextTime);
        //
        //        System.out.println(df.format(nextTime));
        //
        //        //System.out.println(getBizDate("", 0, ""));
        System.out.println(getBizDate("00 */5 04-23 * * ?"));
    }
}
