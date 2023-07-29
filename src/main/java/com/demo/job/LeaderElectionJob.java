package com.demo.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaderElectionJob {
    @Autowired
    private LeaderElectionService electionService;
    @Scheduled(fixedRate = 5000)
    public void printHello() {
        if(electionService.isLeader()) {
            System.out.println("hello leader");
        } else {
            System.out.println("hello worker");
        }

    }
}
