package com.demo;

import com.demo.job.EtcdLeaderElection;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.LeaseOption;
import io.github.zzlgo.etcd.config.annotation.EtcdConfigListener;
import io.github.zzlgo.etcd.config.annotation.EtcdConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EtcdConfigurationProperties
public class DemoApplication {



	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

}
