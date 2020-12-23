package com.bigdata.datashops.server.utils;

import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.between;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.value.SpecialChar;

public class CronHelper {
    public String parse() {
        Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                            .withYear(always())
                            .withDoM(between(SpecialChar.L, 3))
                            .withMonth(always())
                            .withDoW(questionMark())
                            .withHour(always())
                            .withMinute(always())
                            .withSecond(on(0))
                            .instance();
        // Obtain the string expression
        String cronAsString = cron.asString(); // 0 * * L-3 * ? *

        return cronAsString;
    }
}
