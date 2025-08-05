package com.example.medjool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MedjoolApplication {


	public static void main(String[] args) {
		SpringApplication.run(MedjoolApplication.class, args);
	}

}
