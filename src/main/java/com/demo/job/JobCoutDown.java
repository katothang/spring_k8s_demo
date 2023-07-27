package com.demo.job;

import com.demo.config.JobLeader;
import com.demo.enums.TypeServeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobCoutDown extends JobLeader {
    private static final Logger log = LoggerFactory.getLogger(JobCoutDown.class);
    @Override
    public void jobLogic() {
        int seconds = 120;
        for (int i = seconds; i >= 0; i--) {
            try {
                System.out.println("In progress the job is currently running completed after "+i+" second");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Scheduled(fixedRate = 5000L)
    public void excute() throws Exception {
        System.out.println("logic job start");
        setTypeServeEnum(TypeServeEnum.ETCH);
        this.start();
        if(isLeader()) {
            this.jobLogic();
        } else {
            System.out.println("not leader"+ getInstanceLeader());
        }
    }
}
