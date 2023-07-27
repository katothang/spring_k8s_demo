package com.demo.leaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class LeaderLatchEvent implements LeaderLatchListener {

    @Override
    public void isLeader() {
        System.out.println("I am the leader now! 1234");
    }

    @Override
    public void notLeader() {
        System.out.println("I am no longer the leader!");
    }
}
