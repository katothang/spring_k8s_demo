package com.demo.config;

import mousio.etcd4j.EtcdClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class EtcdConfig {
    @Value("${etcd.server}")
    private String etcdServer;

//    @Bean(destroyMethod = "close")
//    public EtcdClient etcdClient() {
//        return new EtcdClient(URI.create(etcdServer));
//    }

    @Bean(name = "curatorEtchFramework")
    public CuratorFramework curatorEtchFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(etcdServer, retryPolicy);
    }
}
