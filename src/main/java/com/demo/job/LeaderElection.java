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

    private final KV kv;
    private final Lease lease;

    public LeaderElection() {
        // Connect to the etcd cluster
        Client client = Client.builder()
                .endpoints("http://54.168.244.189:2379", "http://13.230.230.230:2379", "http://54.168.244.189:2379")
                .build();
        this.kv = client.getKVClient();
        this.lease = client.getLeaseClient();
    }

    public boolean isLeader() {
        try {
            // Request a new lease
            long leaseId = grantLease();

            // Attempt to acquire the leader key with the leaseId within the lease duration
            kv.put(ByteSequence.from(LEADER_KEY, StandardCharsets.UTF_8), ByteSequence.from("your-app", StandardCharsets.UTF_8), PutOption.newBuilder().withLeaseId(leaseId).build()).get();

            // Save leader status in etcd (set value "true" for key "is_leader")
            kv.put(ByteSequence.from(LEADER_STATUS_KEY, StandardCharsets.UTF_8), ByteSequence.from("true", StandardCharsets.UTF_8)).get();

            return true;
        } catch (Exception e) {
            // Handle errors if any
            e.printStackTrace();
            return false;
        }
    }

    // Method for stepping down from leader position
    public void stepDown() {
        try {
            // Remove leader status from etcd (set value "false" for key "is_leader")
            kv.put(ByteSequence.from(LEADER_STATUS_KEY, StandardCharsets.UTF_8), ByteSequence.from("false", StandardCharsets.UTF_8)).get();
        } catch (Exception e) {
            // Handle errors if any
            e.printStackTrace();
        }
    }

    private long grantLease() throws ExecutionException, InterruptedException {
        LeaseGrantResponse leaseGrantResponse = lease.grant(10).get();
        return leaseGrantResponse.getID();
    }
}
