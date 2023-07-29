package com.demo.job;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
public class EtcdLeaderElection {

    private final Client etcdClient;
    private final KeyLeaderModel keyLeaderModel;
    private boolean isLeader = false;

    public EtcdLeaderElection(Client etcdClient, KeyLeaderModel keyLeaderModel) {
        this.etcdClient = etcdClient;
        this.keyLeaderModel = keyLeaderModel;
    }

    public void startLeaderElection() throws ExecutionException, InterruptedException {

        KV kvClient = etcdClient.getKVClient();
        GetResponse keyCheck = kvClient.get(keyLeaderModel.getKey()).get();
        if(keyCheck.getCount() > 0 && !keyLeaderModel.getValue().equals(keyCheck.getKvs().get(0).getValue())) {
            isLeader = false;
            return;
        }
        Lease leaseClient = etcdClient.getLeaseClient();
        // Tạo một lease với thời gian tồn tại là 60 giây
        long leaseId = leaseClient.grant(60).get().getID();

        // Đăng ký khóa "leader-election" với UUID hiện tại và lease đã tạo
        kvClient.put(keyLeaderModel.getKey(), keyLeaderModel.getValue(), PutOption.newBuilder().withLeaseId(leaseId).build()).get();

        // Gửi "KeepAlive" để giữ lease sống.
        new Thread(() -> {
            while (true) {
                try {
                    LeaseKeepAliveResponse response = leaseClient.keepAliveOnce(leaseId).get();
                    long ttl = response.getTTL();
                    GetResponse getFuture = kvClient.get(keyLeaderModel.getKey()).get();
                    if (ttl > 0 && getFuture.getCount() > 0 && keyLeaderModel.getValue().equals(getFuture.getKvs().get(0).getValue())) {
                        // Lease hết hạn, tức là ứng dụng không còn là leader.
                        isLeader = true;
                    } else {
                        isLeader = false;
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
