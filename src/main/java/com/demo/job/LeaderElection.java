package com.demo.job;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
public class LeaderElection {

    String LEADER_KEY = "/your-app/leader";

    private final KV kv;
    private final Lease lease;

    public LeaderElection() {
        LEADER_KEY = UUID.randomUUID().toString();
        // Kết nối đến etcd cluster
        Client client = Client.builder().endpoints("http://54.168.244.189:2379", "http://13.230.230.230:2379", "http://54.168.244.189:2379").build();
        this.kv = client.getKVClient();
        this.lease = client.getLeaseClient();
    }

    public boolean isLeader() {
        try {
            // Tạo mới lease
            long leaseId = grantLease();

            // Cố gắng giữ khóa lãnh đạo với leaseId được cấp phát trong khoảng thời gian lease
            PutResponse response = kv.put(ByteSequence.from(LEADER_KEY, StandardCharsets.UTF_8), ByteSequence.from("your-app", StandardCharsets.UTF_8), PutOption.newBuilder().withLeaseId(leaseId).build()).get();
            if (response.hasPrevKv()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private long grantLease() throws ExecutionException, InterruptedException {
        LeaseGrantResponse leaseGrantResponse = lease.grant(10).get();
        return leaseGrantResponse.getID();
    }
}

