package com.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LeaderOnlyAspect {

    private volatile boolean isLeader = false; // Đây là biến để theo dõi trạng thái leader

    // Setter để cập nhật trạng thái leader
    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    @Around("@annotation(com.demo.annotation.LeaderOnly)")
    public void onlyExecuteForLeader(ProceedingJoinPoint joinPoint) {
        if (!isLeader) {
            System.out.println("I'm not leader, skip leader-only tasks.");
            return;
        }
        try {
            System.out.println("I'm leader, execute leader-only tasks.");
            joinPoint.proceed();
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
        }
    }
}
