package com.bigdata.datashops.server.master.selector;

import org.springframework.stereotype.Component;

import com.bigdata.datashops.model.enums.HostSelector;
import com.bigdata.datashops.model.pojo.job.JobInstance;

@Component
public class SelectorManager {
    public AbstractSelector create(JobInstance instance) {
        HostSelector selector = HostSelector.of(instance.getHostSelector());
        switch (selector) {
            case ASSIGN:
                return new AssignSelector();
            case SCORE:
                return new ScoreSelector();
            default:
                return new RandomHostSelector();
        }
    }
}
