package com.demo.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class JobEtcd {
    @Autowired
    EtcdLeaderElection etcdLeaderElection;

    @Scheduled(fixedRate = 5000) // 5000 milliseconds = 5 seconds
    public void doScheduledTask() throws ExecutionException, InterruptedException {
        // Thực hiện công việc của bạn ở đây
        etcdLeaderElection.startLeaderElection();
        if(etcdLeaderElection.isLeader()) {
            System.out.println("Im a leader");

        } else {
            System.out.println("Im currently a worker. The current leader is "+ etcdLeaderElection.getLeaderId());
        }

    }

}
