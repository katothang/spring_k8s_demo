package com.demo.job;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.LeaseOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class EtcdConfig {
    @Value("${etcd.endpoints}")
    private String etcdEndpoints; // Ví dụ: "http://localhost:2379"
    @Bean
    public Client Client() throws Exception {
        String[] uriList = StringUtils.commaDelimitedListToStringArray(etcdEndpoints);
        Client client = Client.builder().endpoints(uriList).build();
        return client;
    }
}
