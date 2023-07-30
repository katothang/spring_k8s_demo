package com.demo.job;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Component
public class JobHazelcast implements HazelcastInstanceAware {
    @Autowired
    private HazelcastInstance hazelcastInstance;

    private static final String LEADER_MAP_KEY = "leader_hz1";

    @Scheduled(fixedRate = 5000) // In ra 5 giây một lần
    public void printLeaderStatus() throws InterruptedException, UnknownHostException {
        IMap<String, String> leaderMap = hazelcastInstance.getMap(LEADER_MAP_KEY);
        String instanceId = InetAddress.getLocalHost().getHostAddress();
        boolean isLeader = false;

        // Thử đặt lock cho instance hiện tại
        if (leaderMap.tryLock(instanceId, 30, TimeUnit.SECONDS)) {
            try {
                // Kiểm tra xem instance hiện tại có phải là leader không
                if (leaderMap.get(LEADER_MAP_KEY) == null || leaderMap.get(LEADER_MAP_KEY).equals(instanceId)) {
                    leaderMap.put(LEADER_MAP_KEY, instanceId,30, TimeUnit.SECONDS);
                    isLeader = true;
                }
            } finally {
                // Giải phóng lock sau khi xử lý xong
                leaderMap.unlock(instanceId);
            }
        }

        if (isLeader) {
            System.out.println("I'm the leader!");
        } else {
            System.out.println("I'm the member. The currently leader is "+ leaderMap.get(LEADER_MAP_KEY));
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
