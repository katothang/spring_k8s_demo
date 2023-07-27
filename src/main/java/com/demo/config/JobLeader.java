package com.demo.config;

import com.demo.aspect.LeaderOnlyAspect;
import com.demo.enums.TypeServeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class JobLeader extends LeaderSelectorListenerAdapter implements Closeable {

    @Getter
    boolean isLeader = false;

    @Getter
    @Setter
    TypeServeEnum typeServeEnum;

    @Getter
    private boolean isRuning = false;
    @Autowired
    private LeaderSelector selector;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private LeaderOnlyAspect leaderOnlyAspect;

    @Autowired
    @Qualifier("curatorZookeeperFramework")
    CuratorFramework clientZookeper;

    @Autowired
    @Qualifier("curatorEtchFramework")
    CuratorFramework clientEtch;


    public void start() {
        CuratorFramework curatorFramework = null;
        switch (typeServeEnum) {
            case ZOOKEEPER: curatorFramework = clientZookeper; break;
            case ETCH: curatorFramework = clientEtch; break;
            default: curatorFramework = clientZookeper; break;
        }
        selector = new LeaderSelector(curatorFramework, "/leader", this);
        selector.autoRequeue();
        selector.start();
        try {
            System.out.println(selector.getLeader().getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public void takeLeadership(CuratorFramework cf) {
        isLeader = true;
        leaderOnlyAspect.setLeader(true); // Cập nhật trạng thái leader trong Aspect
        myLeaderOnlyTask();
        System.out.println("I'm leader, execute leader-only tasks.");
        if(!isRuning) {
            myLeaderOnlyTask(); // Gọi công việc leader-only ở đây
        } else {
            System.out.println("Job is runing...");
        }
        isRuning = false;
        relinquishLeadership();
    }

    private void relinquishLeadership() {
        isLeader = false;
        leaderOnlyAspect.setLeader(false); // Cập nhật trạng thái leader trong Aspect
        selector.interruptLeadership(); // Chuyển giao quyền leader cho instance khác
    }

     public void myLeaderOnlyTask() {

    }

    public String getInstanceLeader() {
        try {
            return selector.getLeader().getId();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void close() {
        isLeader = false;
        leaderOnlyAspect.setLeader(false); // Cập nhật trạng thái leader trong Aspect khi đóng
        selector.close();
        executorService.shutdownNow();
        isRuning = false;
    }
}
