package com.demo.leaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class LeaderLatchEvent implements LeaderLatchListener {

    private final   DistributedLockManager  lockManager;

    public LeaderLatchEvent(DistributedLockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    public void isLeader() {
        updateLeaderIP();
        System.out.println("I am the leader now! leader ID= "+lockManager.getLeaderIP());
    }

    @Override
    public void notLeader() {
        System.out.println("I am no longer the leader!");
    }

    private void updateLeaderIP() {
        try {
            LeaderLatch leaderLatch = lockManager.getLeaderLatch();
            String leaderId = leaderLatch.getLeader().getId();

            lockManager.setLeaderIP(leaderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
