package com.bigdata.datashops.server.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.server.master.scheduler.Dispatcher;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Component
public class JobQueue {
    private static final Logger LOG = LoggerFactory.getLogger(JobQueue.class);
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private Dispatcher dispatcher;

    private DistributedQueue<String> queue;

    public DistributedQueue<String> getQueue() {
        return queue;
    }

    public void initQueue() {
        try {
            QueueConsumer<String> consumer = createQueueConsumer();
            QueueBuilder<String> builder = QueueBuilder.builder(zookeeperOperator.getZkClient(), consumer,
                    createQueueSerializer(), ZKUtils.getQueuePath());
            queue = builder.buildQueue();
            queue.start();
        } catch (Exception ignored) {

        }
    }

    private QueueSerializer<String> createQueueSerializer() {
        return new QueueSerializer<String>() {
            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }

        };
    }

    private QueueConsumer<String> createQueueConsumer() {
        return new QueueConsumer<String>() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                LOG.info("connection new state: {}", newState.name());
            }

            @Override
            public void consumeMessage(String message) {
                dispatcher.dispatch(message);
            }
        };
    }
}
