package com.demo.distributedLock.zooKeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
//import java.util.concurrent.TimeUnit;

@RestController
public class JobCaller {
    private final JobLockService jobLockService;

    @Autowired
    public JobCaller(JobLockService jobLockService) {
        this.jobLockService = jobLockService;
    }

    @GetMapping("/call-job")
    public String callJob() {
        try {
            //we acquire lock first
            if(jobLockService.acquireLock()){
                System.err.print("D1 is acquired the lock");
                Date dt = new Date();
                System.out.println(dt.toString());

                Thread.sleep(10000);
                jobLockService.releaseLock();
                System.err.print("D1 is released the lock");
                return(dt.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
          //  jobLockService.close();
        }
        return "Job does not excute by locked!";
    }

}
