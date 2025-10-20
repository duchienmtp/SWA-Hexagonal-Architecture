package com.swa.payment_container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.swa.payment_container",
		"com.swa.payment_domain",
		"com.swa.payment_infrastructure",
		"com.swa.payment_application"
})
@EnableJpaRepositories(basePackages = "com.swa.payment_infrastructure.payment_dataaccess.repository")
@EntityScan(basePackages = "com.swa.payment_infrastructure.payment_dataaccess.entity")
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
