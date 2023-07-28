package com.demo.leaderLatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJob {

    private final LeaderService service;


    public ScheduledJob(LeaderService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 5000)
    public void runJob() {
        try {
            if (service.isLeader()) {
                System.out.println("Running the job!");
                // Do the job here
            } else {
                System.out.println("Job skipped. Another instance is the leader.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
