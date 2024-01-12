package com.mjc.school.main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.mjc.school")
@EntityScan(basePackages = {"com.mjc.school.repository.model"})
@Import(SpringfoxConfigurationProperties.Swagger2Configuration.class)
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
