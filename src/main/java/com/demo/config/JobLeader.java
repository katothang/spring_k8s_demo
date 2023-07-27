package com.demo.config;

import com.demo.aspect.LeaderOnlyAspect;
import com.demo.enums.TypeServeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public abstract class JobLeader {

    @Getter
    boolean isLeader = false;

    @Getter
    @Setter
    TypeServeEnum typeServeEnum;

    @Getter
    private boolean isRuning = false;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private LeaderOnlyAspect leaderOnlyAspect;

    @Autowired
    @Qualifier("curatorZookeeperFramework")
    CuratorFramework clientZookeper;

    @Autowired
    @Qualifier("curatorEtchFramework")
    CuratorFramework clientEtch;
    private LeaderLatch leaderLatch;


    public void start() throws Exception {
        CuratorFramework curatorFramework = null;
        switch (typeServeEnum) {
            case ZOOKEEPER: curatorFramework = clientZookeper; break;
            case ETCH: curatorFramework = clientEtch; break;
            default: curatorFramework = clientZookeper; break;
        }
        if(!curatorFramework.isStarted()) {
            curatorFramework.start();
        }

        String leaderPath = "/locks/leader";

        leaderLatch = new LeaderLatch(curatorFramework, leaderPath, String.format("ip_%s_key_%s", InetAddress.getLocalHost().getHostAddress(), UUID.randomUUID().toString()));
        leaderLatch.start();
        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                System.out.println("Leader");
                //jobLogic();
            }

            @Override
            public void notLeader() {
                System.out.println("Not Leader");
            }
        });
    }

    private void relinquishLeadership() {
        isLeader = false;
        leaderOnlyAspect.setLeader(false); // Cập nhật trạng thái leader trong Aspect

    }

    public boolean isLeader() {
        return leaderLatch.hasLeadership();
    }
     public abstract void  jobLogic();
    public String getInstanceLeader() {
        try {
            return leaderLatch.getLeader().getId();
        } catch (Exception e) {
            return "";
        }
    }

//    @Override
//    public void close() {
//        isLeader = false;
//        leaderOnlyAspect.setLeader(false); // Cập nhật trạng thái leader trong Aspect khi đóng
//        selector.close();
//        executorService.shutdownNow();
//        isRuning = false;
//    }
}
