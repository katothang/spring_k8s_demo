package com.demo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("")
public class DemoController {
    @GetMapping("")
    public String getInfo() throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        return "Demo k8s cluster with 3 nodes runing with ip = "+ip;
    }
}
