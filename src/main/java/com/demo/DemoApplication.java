package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

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
