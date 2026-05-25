package com.GameHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsvcReview {
    public static void main(String[] args) {
        SpringApplication.run(MsvcReview.class, args);
    }
}