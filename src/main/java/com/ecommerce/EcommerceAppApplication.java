package com.ecommerce;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EcommerceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceAppApplication.class, args);


	}
	@Bean
	ApplicationRunner checkFlyway(ApplicationContext context) {
		return args -> {
			System.out.println(
					"Flyway bean exists: " +
							context.containsBean("flyway")
			);
		};
	}

}
