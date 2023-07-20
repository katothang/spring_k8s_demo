package com.demo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class DemoController {
    @GetMapping("")
    public String getInfo() {
        return "Project spring boot deploy demo hello everyone";
    }
}
