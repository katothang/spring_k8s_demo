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
import java.util.concurrent.ExecutionException;

@Component
public class LeaderElection {

    private final String LEADER_KEY = "leader";
    private final String LEADER_STATUS_KEY = "is_leader";
    private final long LEASE_TIMEOUT = 10; // Lease timeout in seconds

    private final KV kv;
    private final Lease lease;

    public LeaderElection() {
        // Kết nối đến etcd cluster
        Client client = Client.builder()
                .endpoints("http://54.168.244.189:2379", "http://13.230.230.230:2379", "http://54.168.244.189:2379")
                .build();
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
                // Nếu key đã tồn tại, tức là không phải lãnh đạo
                // Xóa trạng thái lãnh đạo trong etcd (set giá trị "false" cho key "is_leader")
                kv.put(ByteSequence.from(LEADER_STATUS_KEY, StandardCharsets.UTF_8), ByteSequence.from("false", StandardCharsets.UTF_8)).get();
                return false;
            } else {
                // Nếu key không tồn tại, chứng tỏ đã giành được lãnh đạo
                // Lưu trạng thái lãnh đạo vào etcd (set giá trị "true" cho key "is_leader")
                kv.put(ByteSequence.from(LEADER_STATUS_KEY, StandardCharsets.UTF_8), ByteSequence.from("true", StandardCharsets.UTF_8)).get();
                return true;
            }
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            e.printStackTrace();
            return false;
        }
    }

    private long grantLease() throws ExecutionException, InterruptedException {
        LeaseGrantResponse leaseGrantResponse = lease.grant(LEASE_TIMEOUT).get();
        return leaseGrantResponse.getID();
    }
}
