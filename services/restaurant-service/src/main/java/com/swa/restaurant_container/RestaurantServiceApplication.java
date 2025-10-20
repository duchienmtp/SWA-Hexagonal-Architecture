package com.swa.restaurant_container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.swa.restaurant_container",
		"com.swa.restaurant_domain",
		"com.swa.restaurant_infrastructure",
		"com.swa.restaurant_application"
})
@EnableJpaRepositories(basePackages = "com.swa.restaurant_infrastructure.restaurant_dataaccess.repository")
@EntityScan(basePackages = "com.swa.restaurant_infrastructure.restaurant_dataaccess.entity")
public class RestaurantServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestaurantServiceApplication.class, args);
	}

}
