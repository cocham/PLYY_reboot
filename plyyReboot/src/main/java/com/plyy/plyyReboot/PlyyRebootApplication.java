package com.plyy.plyyReboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PlyyRebootApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlyyRebootApplication.class, args);
	}

}
