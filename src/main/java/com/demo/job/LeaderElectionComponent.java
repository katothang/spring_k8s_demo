package com.demo.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.cluster.etcd.leader.LeaderInitiator;
import org.springframework.cloud.cluster.leader.event.DefaultLeaderEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaderElectionComponent {
    @Autowired
    private LeaderInitiator leaderInitiator;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void startApplication() {
        System.out.println("start job");
        leaderInitiator.start();
        if(leaderInitiator.isRunning()) {
            System.out.println("hello leader");
        } else {
            System.out.println("hello not leader");
        }

    }
}
