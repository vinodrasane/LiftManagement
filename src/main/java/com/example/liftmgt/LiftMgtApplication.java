package com.example.liftmgt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class LiftMgtApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiftMgtApplication.class, args);
	}
}
