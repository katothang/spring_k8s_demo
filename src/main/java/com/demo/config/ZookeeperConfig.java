package com.demo.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

@Configuration
public class ZookeeperConfig {
    @Value("${zookeeper.server}")
    private String zookeeperServer;
    @Value("${zookeeper.leaderPath}")
    private String leaderPath;

    @Bean(name = "curatorZookeeperFramework")
    @Primary
    public CuratorFramework curatorFramework() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperServer, new ExponentialBackoffRetry(1000, 3));
        client.start();
        return client;
    }

    @Bean
    public LeaderSelector leaderSelector(CuratorFramework curatorFramework) {
        return new LeaderSelector(curatorFramework, "/leader", new JobLeader());
    }

    @Bean
    public LeaderLatch leaderLatch(CuratorFramework curatorFramework) throws Exception  {
        if (curatorFramework.checkExists().forPath(leaderPath) == null) {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(leaderPath);
        }
        LeaderLatch leaderLatch = new LeaderLatch(curatorFramework, leaderPath, UUID.randomUUID().toString());
        leaderLatch.start();
        leaderLatch.addListener(leaderLatchListener());
        return leaderLatch;
    }

    @Bean
    public LeaderLatchListener leaderLatchListener() {
        return new LeaderLatchListener() {
            @Override
            public void isLeader() {
                System.out.println("I am the leader!");
            }

            @Override
            public void notLeader() {
                System.out.println("I am not the leader.");
            }
        };
    }
}
