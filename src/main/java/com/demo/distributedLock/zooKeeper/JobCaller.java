package com.demo.distributedLock.zooKeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
            // Kiểm tra xem lock có được giữ hay không
            if (jobLockService.isLockAcquired()) {
                // Nếu lock được giữ, thực hiện công việc
                System.out.println("Job called successfully.");
                return "Job called successfully.";
            } else {
                // Nếu lock không được giữ, không thực hiện công việc
                System.out.println("Lock is not acquired, cannot process the job.");
                return "Lock is not acquired, cannot process the job.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while processing the job.";
        }
    }
}
