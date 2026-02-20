package com.woojin.prography_assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PrographyAssignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrographyAssignmentApplication.class, args);
    }

}
