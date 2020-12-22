package com.bigdata.datashops.server.master.scheduler;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.Job;
import com.bigdata.datashops.model.pojo.JobGraph;
import com.bigdata.datashops.model.pojo.JobInstance;
import com.bigdata.datashops.service.JobGraphService;
import com.bigdata.datashops.service.JobService;

@Service
public class Checker {
    @Autowired
    private JobGraphService jobGraphService;

    @Autowired
    private JobService jobService;

    public boolean check(JobInstance jobInstance) {
        String preDependency = jobInstance.getPreDependency();
        if (StringUtils.isBlank(preDependency)) {
            return true;
        }
        Date bizTime = jobInstance.getBizTime();
        String[] dependencies = preDependency.split(Constants.SEPARATOR_COMMA);
        for (int i = 0; i < dependencies.length; i++) {
            String[] dependency = dependencies[i].split(Constants.SEPARATOR_LINE);
            Integer preJobId = Integer.valueOf(dependency[0]);
            Integer offset = Integer.valueOf(dependency[1]);
            Job preJob = jobService.getJob(preJobId);
            JobGraph jobGraph = jobGraphService.getJobGraph(preJobId);
            int schedulingPeriod = jobGraph.getSchedulingPeriod();
            switch (SchedulingPeriod.of(schedulingPeriod)) {
                case MINUTE:
                case HOUR:
                case DAY:
                case WEEK:
                case MONTH:
                default:
            }


        }
        return true;
    }

    private Date getLastDayBizTime(Date date) {

    }
}
