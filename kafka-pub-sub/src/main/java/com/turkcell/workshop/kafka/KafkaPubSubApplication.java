package com.turkcell.workshop.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class KafkaPubSubApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaPubSubApplication.class, args);
    }

}
