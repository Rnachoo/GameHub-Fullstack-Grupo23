package com.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcPromotion {

    public static void main(String[] args) {
        SpringApplication.run(MsvcPromotion.class, args);
    }

}
