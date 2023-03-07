package com.hyuuny.nonfaceauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class NonFaceAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NonFaceAuthServiceApplication.class, args);
    }

}
