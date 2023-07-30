package com.demo.job;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;


@Component
public class JobHazelcast {
    @Autowired
    HazelcastInstance hazelcastInstance;

    private static final String LEADER_MAP_KEY = "leader";

    @Scheduled(fixedRate = 5000) // In ra 5 giây một lần
    public void printLeaderStatus() {
        IMap<String, String> leaderMap = hazelcastInstance.getMap(LEADER_MAP_KEY);
        String instanceId = hazelcastInstance.getLocalEndpoint().getUuid().toString();
        boolean isLeader = false;

        // Try to set the leader flag for this instance
        if (leaderMap.tryPut(instanceId, "leader", 0, java.util.concurrent.TimeUnit.SECONDS)) {
            isLeader = true;
            // Do something as a leader
        } else {
            // Do something as a worker
            isLeader = false;
        }

        if (isLeader) {
            System.out.println("I'm the leader!");
        } else {
            System.out.println("I'm the member");
        }
    }


}
