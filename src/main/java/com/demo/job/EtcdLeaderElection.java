package com.demo.job;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.LeaseOption;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Component
public class EtcdLeaderElection {

    private final Client etcdClient;
    private boolean isLeader = false;

    public EtcdLeaderElection(Client etcdClient) {
        this.etcdClient = etcdClient;
    }

    public void startLeaderElection() throws ExecutionException, InterruptedException {
        KV kvClient = etcdClient.getKVClient();
        Lease leaseClient = etcdClient.getLeaseClient();

        // Đăng ký khóa với lease và giữ ngữ cảnh.
        ByteSequence key = ByteSequence.from("leader-election".getBytes(StandardCharsets.UTF_8));
        ByteSequence value = ByteSequence.from("This is the leader".getBytes(StandardCharsets.UTF_8));

        kvClient.put(key, value).get();

        // Gửi "KeepAlive" để giữ lease sống.
        new Thread(() -> {
            while (true) {
                LeaseKeepAliveResponse response;
                try {
                    long leaseId = etcdClient.getLeaseClient().grant(60).get().getID();
                    response = leaseClient.keepAliveOnce(leaseId).get();
                    long ttl = response.getTTL();
                    if (ttl <= 0) {
                        // Lease hết hạn, tức là ứng dụng không còn là leader.
                        isLeader = false;
                    } else if (!isLeader) {
                        // Chưa phải leader và lease vẫn còn hiệu lực, tức là ứng dụng trở thành leader.
                        isLeader = true;
                        System.out.println("I'm the leader!");
                    }
                    Thread.sleep(ttl * 1000 / 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean isLeader() {
        return isLeader;
    }
}
