package com.demo.job;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.options.LeaseOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Configuration
public class EtcdConfig {
    @Value("${etcd.endpoints}")
    private String etcdEndpoints;

    @Bean
    public KeyLeaderModel getInstanceLeaderModel() {
        String instanceId = UUID.randomUUID().toString();
        ByteSequence key = ByteSequence.from("leader".getBytes(StandardCharsets.UTF_8));
        ByteSequence value = ByteSequence.from(instanceId.getBytes(StandardCharsets.UTF_8));
        return new KeyLeaderModel(key, value);

    }
    @Bean
    public Client Client() throws Exception {
        String[] uriList = StringUtils.commaDelimitedListToStringArray(etcdEndpoints);
        Client client = Client.builder().endpoints(uriList).build();
        return client;
    }
}
