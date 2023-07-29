package com.demo.job;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
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

        // Tạo một UUID ngẫu nhiên cho mỗi instance và sử dụng nó làm khóa.
        String instanceId = UUID.randomUUID().toString();
        ByteSequence key = ByteSequence.from("leader-election".getBytes(StandardCharsets.UTF_8));
        ByteSequence value = ByteSequence.from(instanceId.getBytes(StandardCharsets.UTF_8));

        // Tạo một lease với thời gian tồn tại là 60 giây
        long leaseId = leaseClient.grant(60).get().getID();

        // Đăng ký khóa "leader-election" với UUID hiện tại và lease đã tạo
        kvClient.put(key, value, PutOption.newBuilder().withLeaseId(leaseId).build()).get();

        // Gửi "KeepAlive" để giữ lease sống.
        new Thread(() -> {
            while (true) {
                try {
                    LeaseKeepAliveResponse response = leaseClient.keepAliveOnce(leaseId).get();
                    long ttl = response.getTTL();
                    if (ttl <= 0) {
                        // Lease hết hạn, tức là ứng dụng không còn là leader.
                        isLeader = false;
                    } else if (!isLeader) {
                        // Chưa phải leader và lease vẫn còn hiệu lực, tức là ứng dụng trở thành leader.
                        isLeader = true;
                        System.out.println("I'm the leader! Instance ID: " + instanceId);
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
