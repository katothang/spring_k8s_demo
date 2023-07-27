package com.demo.job;

import com.demo.config.JobLeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JobCoutDown extends JobLeader {
    private static final Logger log = LoggerFactory.getLogger(JobCoutDown.class);
    @Override
    public void myLeaderOnlyTask() {
        int seconds = 120;

        for (int i = seconds; i >= 0; i--) {
            try {
                log.info("In progress the job is currently running completed after "+i+" second");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
