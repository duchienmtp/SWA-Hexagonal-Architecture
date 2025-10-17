package com.swa.customer_container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
	"com.swa.customer_container",
	"com.swa.customer_domain",
	"com.swa.customer_infrastructure",
	"com.swa.customer_application"
})
@EnableMongoRepositories(basePackages = "com.swa.customer_infrastructure.customer_dataaccess.repository")
@EntityScan(basePackages = "com.swa.customer_infrastructure.customer_dataaccess.entity")
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
