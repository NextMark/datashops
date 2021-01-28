package com.bigdata.datashops.server.master.dispatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bigdata.datashops.model.enums.HostSelector;
import com.bigdata.datashops.server.master.dispatch.selector.HostManager;
import com.bigdata.datashops.server.master.dispatch.selector.RandomHostSelector;
import com.bigdata.datashops.server.master.dispatch.selector.ScoreSelector;
import com.google.common.collect.Lists;

@Configuration
public class HostManagerConfig {
    @Bean
    public HostManager hostManager() {
        HostSelector selector = HostSelector.of(0);
        HostManager hostManager;
        switch (selector) {
            case RANDOM:
                hostManager = new RandomHostSelector(Lists.newArrayList());
                break;
            case SCORE:
                hostManager = new ScoreSelector();
                break;
            default:
                throw new IllegalArgumentException("unSupport selector " + selector);
        }
        return hostManager;
    }
}
