package com.demo.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class LeaderSelectionConfig implements CommandLineRunner {

    @Autowired
    private LeaderElectionService electionService;

    @Override
    public void run(String... args) {
        // Attempt to become the leader on startup
        electionService.electLeader();
    }
}

