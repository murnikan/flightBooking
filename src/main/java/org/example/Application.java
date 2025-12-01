package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "org.example",
        "Users",
        "airlines",
        "airports",
        "planes"
})
@EntityScan(basePackages = {
        "org.example",
        "Users",
        "airlines",
        "airports",
        "planes"
})
@EnableJpaRepositories(basePackages = {
        "org.example",
        "Users",
        "airlines",
        "airports",
        "planes"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
