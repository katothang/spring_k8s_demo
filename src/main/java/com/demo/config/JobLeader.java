package com.demo.config;

import com.demo.aspect.LeaderOnlyAspect;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class JobLeader extends LeaderSelectorListenerAdapter implements Closeable {

    private volatile boolean isLeader = false;

    public static  boolean isRuning = false;
    @Autowired
    private LeaderSelector selector;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private LeaderOnlyAspect leaderOnlyAspect;

    @Autowired
    CuratorFramework client;


    public void start() {
        selector = new LeaderSelector(client, "/leader", this);
        selector.autoRequeue();
        selector.start();
    }


    @Override
    public void takeLeadership(CuratorFramework cf) {
        isLeader = true;
        leaderOnlyAspect.setLeader(true); // Cập nhật trạng thái leader trong Aspect

        // Thực thi công việc leader-only trong một luồng riêng biệt
        executorService.submit(() -> {
            System.out.println("I'm leader, execute leader-only tasks.");
            if(!isRuning) {
                isRuning = true;
                myLeaderOnlyTask(); // Gọi công việc leader-only ở đây
                isRuning = false;

            } else {
                System.out.println("Job is runing...");
            }

            relinquishLeadership();
        });
    }

    private void relinquishLeadership() {
        isLeader = false;
        leaderOnlyAspect.setLeader(false); // Cập nhật trạng thái leader trong Aspect
        selector.interruptLeadership(); // Chuyển giao quyền leader cho instance khác
    }

     public void myLeaderOnlyTask() {

    }

    @Override
    public void close() {
        isLeader = false;
        leaderOnlyAspect.setLeader(false); // Cập nhật trạng thái leader trong Aspect khi đóng
        selector.close();
        executorService.shutdownNow();
    }
}
