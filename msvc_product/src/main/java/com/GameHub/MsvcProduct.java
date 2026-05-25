package com.GameHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcProduct {

    public static void main(String[] args) {
        SpringApplication.run(MsvcProduct.class, args);
    }

}
