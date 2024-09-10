package com.example.chatserveruser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ChatServerUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerUserApplication.class, args);
    }

}
