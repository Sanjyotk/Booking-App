package com.SpringBoot.Sanjyot.AirbnbClone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirbnbCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirbnbCloneApplication.class, args);
	}

}
