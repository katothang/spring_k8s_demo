package com.demo.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HelloJob {

    @Autowired
    private LeaderElection leaderElection;

    @Scheduled(fixedRate = 5000)
    public void printHello() {
        System.out.println("Start job");
        if (leaderElection.isLeader()) {
            System.out.println("Hello - I am the leader");
        } else {
            System.out.println("Hello - I am not the leader");
        }
    }
}

