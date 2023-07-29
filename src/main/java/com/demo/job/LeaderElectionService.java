package com.demo.job;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.kv.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Service
public class LeaderElectionService {

    private static final String LEADER_KEY = "/leader";
    private static final int LEADER_LEASE_TTL = 5; // TTL in seconds

    @Autowired
    private Client etcdClient;

    private boolean isLeader = false;

    public void electLeader() {
        try {
            // Grant a lease to the current instance
            LeaseGrantResponse leaseGrantResponse = etcdClient.getLeaseClient().grant(LEADER_LEASE_TTL).get();
            long leaseId = leaseGrantResponse.getID();

            // Try to put the current instance as the leader using the lease
            etcdClient.getKVClient().put(toBytes(LEADER_KEY), toBytes("leader"), PutOption.newBuilder().withLeaseId(leaseId).build()).get();
            isLeader = true;

            System.out.println("This instance is the leader.");
        } catch (InterruptedException | ExecutionException e) {
            isLeader = false;
            System.out.println("Failed to become the leader: " + e.getMessage());
        }
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void revokeLeadership() {
        try {
            etcdClient.getKVClient().delete(toBytes(LEADER_KEY)).get();
            isLeader = false;
            System.out.println("Leadership revoked.");
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to revoke leadership: " + e.getMessage());
        }
    }

    private ByteSequence toBytes(String str) {
        return ByteSequence.from(str, StandardCharsets.UTF_8);
    }
}

