package com.demo.leaderLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaderService {

    private final LeaderLatch leaderLatch;

    @Autowired
    public LeaderService(LeaderLatch leaderLatch) {
        this.leaderLatch = leaderLatch;
    }

    public boolean isLeader() {
        return leaderLatch.hasLeadership();
    }
}
