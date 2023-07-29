package com.demo.job;

import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfiguration {

    @Value("${etcd.endpoints}")
    private String endpoints;

    @Bean
    public Client etcdClient() {
        return Client.builder().endpoints(endpoints.split(",")).build();
    }
}

