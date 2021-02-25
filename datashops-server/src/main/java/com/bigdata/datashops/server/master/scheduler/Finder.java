package com.bigdata.datashops.server.master.scheduler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.redis.RedissonDistributeLocker;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.bigdata.datashops.service.JobInstanceService;
import com.google.common.collect.Lists;

@Component
public class Finder implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    RedissonDistributeLocker redissonDistributeLocker;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private Checker checker;

    @Override
    public void run() {
        LOG.info("Finder run.");
        DistributedQueue<String> queue = null;
        try {
            QueueConsumer<String> consumer = zookeeperOperator.createQueueConsumer();
            QueueBuilder<String> builder = QueueBuilder.builder(zookeeperOperator.getZkClient(), consumer,
                    zookeeperOperator.createQueueSerializer(), ZKUtils.getQueuePath());
            queue = builder.buildQueue();
            queue.start();
            for (int i = 0; i < 10; i++) {
                int priority = (int) (Math.random() * 100);
                queue.put("test-");
            }
        } catch (Exception e) {

        } finally {
            CloseableUtils.closeQuietly(queue);
        }
        InterProcessMutex mutex = null;
        try {
            mutex = new InterProcessMutex(zookeeperOperator.getZkClient(), ZKUtils.getFinderLockPath());
            mutex.acquire();
            List<Integer> status = Lists.newArrayList();
            for (RunState runState : RunState.values()) {
                if (RunState.WAIT_FOR_DEPENDENCY.compareTo(runState) < 0) {
                    break;
                }
                status.add(runState.getCode());
            }
            String filters = "state=" + StringUtils.join(status, Constants.SEPARATOR_COMMA);

            List<JobInstance> jobInstanceList = jobInstanceService.findReadyJob(filters);
            if (jobInstanceList.size() > 0) {
                LOG.info("[Finder] find {} instances, add to queue", jobInstanceList.size());
            }
            for (JobInstance instance : jobInstanceList) {
                RunState state = checker.checkJob(instance);
                //                if (state == RunState.WAIT_FOR_RUN) {
                //                    zookeeperOperator
                //                            .persistPersistentSequential(ZKUtils.getQueuePath() + "/ttt-", instance
                //                            .getMaskId());
                //                }
            }
        } catch (Exception e) {
            LOG.error("master start up exception", e);
        } finally {
            zookeeperOperator.releaseMutex(mutex);
        }
    }
}
