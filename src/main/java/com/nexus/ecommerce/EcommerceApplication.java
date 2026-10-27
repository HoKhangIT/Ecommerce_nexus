package com.nexus.ecommerce;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcommerceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("🚀 Nexus Ecommerce Application is running! 🚀");
	}
}
