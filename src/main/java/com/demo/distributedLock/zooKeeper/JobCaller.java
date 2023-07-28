package com.demo.distributedLock.zooKeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

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
            // Giữ khóa trước khi thực hiện công việc
            if (jobLockService.acquireLock(30, TimeUnit.SECONDS)) {
                try {
                    // Nếu giữ khóa thành công, thực hiện công việc
                    System.out.println("Job called successfully.");
                    Thread.sleep(10000);
                    return "Job called successfully.";
                } finally {
                    // Giải phóng khóa sau khi hoàn thành công việc
                    jobLockService.releaseLock();
                }
            } else {
                // Nếu không giữ khóa được, không thực hiện công việc
                System.out.println("Lock is not acquired, cannot process the job.");
                return "Lock is not acquired, cannot process the job.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while processing the job.";
        }
    }
}
