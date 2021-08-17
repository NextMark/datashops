package com.bigdata.datashops.service.utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

    public static Set<JobDependency> split(JobDependency dependency) {
        Set<JobDependency> dependencyList = Sets.newHashSet();
        int type = dependency.getType();
        List<Integer> offsets = JobHelper.parseOffsetToList(dependency.getOffset(), type);
        for (Integer o : offsets) {
            JobDependency jobDependency = new JobDependency();
            jobDependency.setSourceId(dependency.getSourceId());
            jobDependency.setTargetId(dependency.getTargetId());
            jobDependency.setOffset(String.valueOf(o));
            dependencyList.add(jobDependency);
        }
        return dependencyList;
    }

    public static List<Integer> parseOffsetToList(String offset, int type) {
        String[] offsetRegion = offset.split(Constants.SEPARATOR_COMMA);
        List<Integer> offsets = Lists.newArrayList();
        if (type == 1) {
            offsets = Arrays.stream(offsetRegion).map(Integer::valueOf).collect(Collectors.toList());
        } else if (type == 2) {
            int begin = Integer.parseInt(offsetRegion[0]);
            int end = Integer.parseInt(offsetRegion[1]);

            for (int i = begin; i < end; i++) {
                offsets.add(i);
            }
        }
        return offsets;
    }

}
