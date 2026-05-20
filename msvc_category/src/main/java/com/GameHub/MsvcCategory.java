package com.GameHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcCategory {

    public static void main(String[] args) {
        SpringApplication.run(MsvcCategory.class, args);
    }

}
