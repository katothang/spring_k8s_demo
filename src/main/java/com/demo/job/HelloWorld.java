package com.demo.job;

import com.demo.annotation.LeaderOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HelloWorld {

    private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);

    //@Scheduled(fixedRate = 50000L)
    //@LeaderOnly
    public void sayHello() {

        int seconds = 120;

        for (int i = seconds; i >= 0; i--) {
            System.out.println(i);
            try {
                log.info("In progress the job is currently running completed after "+i+" second");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
