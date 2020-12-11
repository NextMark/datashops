package com.bigdata.datashops.server.master.scheduler;

import static com.bigdata.datashops.common.Constants.ZK_JOB_QUEUE;
import static com.bigdata.datashops.common.Constants.ZK_ROOT;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.bigdata.datashops.service.JobInstanceService;

@Component
public class Dispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Scheduled(cron = "*/10 * * * * ?")
    public void dispatch() {
        List<String> ready = zookeeperOperator.getChildrenKeys(ZK_ROOT + ZK_JOB_QUEUE);
    }
}
