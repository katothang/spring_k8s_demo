package com.demo.controller;


import com.demo.enums.TypeServeEnum;
import com.demo.job.HelloWorld;
import com.demo.job.JobCoutDown;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("")
public class DemoController {
    @Autowired
    JobCoutDown jobCoutDown;
    @GetMapping("")
    public String getInfo() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        return "v2 Demo k8s cluster with 3 nodes runing with ip = "+ip;
    }

    @GetMapping("excute")

    public String excute() throws Exception {
        jobCoutDown.setTypeServeEnum(TypeServeEnum.ZOOKEEPER);
        jobCoutDown.start();
        return jobCoutDown.isLeader() ?  "Job is Running instance "+ jobCoutDown.getInstanceLeader() : "Job start excute..";
    }


    @GetMapping("test")
    public String testConnection() {
        int SESSION_TIMEOUT = 3000;
        String CONNECT_STRING = "54.168.244.189:2181";
        try {
            ZooKeeper zooKeeper = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("Connection state: " + event.getState());
                }
            });

            // Chờ cho kết nối thành công
            while (zooKeeper.getState() != ZooKeeper.States.CONNECTED) {
                Thread.sleep(100);
            }

            // Kết nối thành công
            System.out.println("Connected to ZooKeeper server: " + CONNECT_STRING);

            // Đóng kết nối khi không sử dụng nữa
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to ZooKeeper server: " + CONNECT_STRING);
        }
        return "";
    }

}
