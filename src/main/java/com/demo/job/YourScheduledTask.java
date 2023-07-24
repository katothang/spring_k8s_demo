package com.demo.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class YourScheduledTask {

    @Scheduled(fixedRate = 5000)
    public void yourTask() {
        System.out.println("Running your task every 5 second.");
    }
}