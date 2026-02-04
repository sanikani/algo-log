package org.sani.algolog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class AlgoLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlgoLogApplication.class, args);
    }

}
