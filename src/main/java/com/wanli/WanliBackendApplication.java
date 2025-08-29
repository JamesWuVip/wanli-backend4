package com.wanli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 万里后端应用主启动类
 * 
 * @author wanli
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.wanli.repository")
@EntityScan(basePackages = "com.wanli.entity")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class WanliBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WanliBackendApplication.class, args);
    }

}