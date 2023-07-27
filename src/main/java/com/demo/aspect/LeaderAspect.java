package com.demo.aspect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
@Aspect
@Component
public class LeaderAspect extends LeaderSelectorListenerAdapter implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(LeaderAspect.class);
    private static final long TENURE_MS = 2000L;
    private static final String ELECTION_ROOT = "/election";

    private volatile boolean isLeader = false;
    private final LeaderSelector selector;

    @Autowired
    public LeaderAspect(CuratorFramework client) {
        selector = new LeaderSelector(client, ELECTION_ROOT, this);
        selector.autoRequeue();
        selector.start();
    }

    public void takeLeadership(CuratorFramework cf) {
        isLeader = true;
        try {
            cf.wait();
        } catch (InterruptedException ex) {
            // nothing to do
        }

        revokeLeadership();
    }

    private void revokeLeadership() {
        isLeader = false;
    }

    @Override
    public void close() {
        revokeLeadership();
    }

    @Around("@annotation(com.demo.annotation.LeaderOnly)")
    public void onlyExecuteForLeader(ProceedingJoinPoint joinPoint) {
        if (!isLeader) {
            log.info("I'm not leader, skip leader-only tasks.");
            return;
        }

        try {
            log.info("I'm leader, execute leader-only tasks.");
            joinPoint.proceed();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }
}