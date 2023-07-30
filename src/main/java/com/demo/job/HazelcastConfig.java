package com.demo.job;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();

        // Set up the TCP-IP join configuration
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getTcpIpConfig().setEnabled(true);
        joinConfig.getTcpIpConfig().addMember("54.168.244.189:5701");
        joinConfig.getTcpIpConfig().addMember("13.230.230.230:5701");
        joinConfig.getTcpIpConfig().addMember("54.95.182.209:5701");

        return Hazelcast.newHazelcastInstance(config);
    }
}
