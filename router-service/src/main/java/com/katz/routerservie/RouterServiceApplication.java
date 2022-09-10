package com.katz.routerservie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RouterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouterServiceApplication.class, args);
	}

}
