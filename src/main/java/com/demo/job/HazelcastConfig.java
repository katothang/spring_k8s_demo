package com.demo.job;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("my-hazelcast-cluster"); // Đảm bảo tên cụm khớp với tên cụm trên Hazelcast Server

        // Cấu hình địa chỉ IP và cổng của các thành viên trong cụm Hazelcast
        clientConfig.getNetworkConfig().addAddress("54.168.244.189:5701", "13.230.230.230:5701", "54.95.182.209:5701");

        // Tạo Hazelcast Client và kết nối tới cụm Hazelcast
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        return hazelcastInstance;
    }
}
