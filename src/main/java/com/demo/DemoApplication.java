package com.demo;

import com.demo.job.LeaderElection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.cluster.etcd.leader.LeaderInitiator;
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DemoApplication {

	private static final String LEADER_KEY = "/your-app/leader";
	private static volatile boolean isLeader = false;
	private static volatile long leaseId = 0;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
