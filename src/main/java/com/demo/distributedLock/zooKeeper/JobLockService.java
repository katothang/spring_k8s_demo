package com.demo.distributedLock.zooKeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JobLockService {
    private static final String LOCK_PATH = "/locks/my_mutex_lock";

    private final CuratorFramework curatorFramework;
    private final InterProcessMutex lock;
    private final NodeCache nodeCache;

    @Autowired
    public JobLockService(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        this.lock = new InterProcessMutex(curatorFramework, LOCK_PATH);
        this.nodeCache = new NodeCache(curatorFramework, LOCK_PATH);
        try {
            nodeCache.start();
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    // Xử lý sự kiện thay đổi node, ví dụ như console log node đang giữ khóa
                    if (lock.isAcquiredInThisProcess()) {
                        System.out.println("Lock is acquired by this node: " + nodeCache.getCurrentData().getPath());
                    } else {
                        System.out.println("Lock is not acquired by this node.");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean acquireLock(long timeout, TimeUnit unit) {
        try {
            return lock.acquire(timeout, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void releaseLock() {
        try {
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLockAcquired() {
        try {
            return lock.isAcquiredInThisProcess();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        curatorFramework.close();
    }
}

