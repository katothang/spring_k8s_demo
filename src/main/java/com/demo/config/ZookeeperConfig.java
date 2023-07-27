package com.demo.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ZookeeperConfig {
    @Value("${zookeeper.server}")
    private String zookeeperServer;
    @Bean(name = "curatorZookeeperFramework")
    @Primary
    public CuratorFramework curatorFramework() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperServer, new ExponentialBackoffRetry(1000, 3));
        client.start();
        return client;
    }

}
