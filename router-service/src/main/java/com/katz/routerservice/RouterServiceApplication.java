package com.katz.routerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class RouterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouterServiceApplication.class, args);
    }

}
