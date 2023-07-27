package com.demo.leaderLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.util.UUID;

@Component
public class DistributedLockManager implements ApplicationRunner, Closeable {

    @Value("${zookeeper.host}")
    private String zookeeperHost;

    private CuratorFramework curatorFramework;
    private LeaderLatch leaderLatch;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperHost, new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();

        String leaderPath = "/locks/leader";
        leaderLatch = new LeaderLatch(curatorFramework, leaderPath, UUID.randomUUID().toString());
        leaderLatch.start();

        // Add a listener to handle leadership change events
        leaderLatch.addListener(new LeaderLatchEvent());
    }

    public boolean isLeader() {
        return leaderLatch.hasLeadership();
    }

    @PreDestroy
    @Override
    public void close() {
        // Close the leader latch and release the lock when the application is shut down
        try {
            leaderLatch.close();
            curatorFramework.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
