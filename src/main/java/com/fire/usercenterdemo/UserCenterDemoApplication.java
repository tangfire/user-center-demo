package com.fire.usercenterdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fire.usercenterdemo.mapper")
public class UserCenterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterDemoApplication.class, args);
    }

}
