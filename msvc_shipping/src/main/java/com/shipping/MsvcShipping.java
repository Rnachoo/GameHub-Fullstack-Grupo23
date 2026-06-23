package com.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsvcShipping {
    public static void main(String[] args) {
        SpringApplication.run(MsvcShipping.class, args);
    }
}