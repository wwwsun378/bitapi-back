package com.bitservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//服务发现客户端
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class BackstageApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackstageApplication.class, args);
	}
}
