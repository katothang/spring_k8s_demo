package com.demo.leaderLatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJob {

    private final DistributedLockManager lockManager;

    public ScheduledJob(DistributedLockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Scheduled(fixedRate = 5000)
    public void runJob() {
        try {
            if (lockManager.isLeader()) {
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
