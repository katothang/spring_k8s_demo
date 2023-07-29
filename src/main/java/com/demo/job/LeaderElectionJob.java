package com.demo.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaderElectionJob {

    @Scheduled(fixedRate = 5000)
    public void printHello() {
        System.out.println("hello");
    }
}
