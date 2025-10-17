package com.swa.order_container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.swa.order_container",
		"com.swa.order_domain",
		"com.swa.order_infrastructure",
		"com.swa.order_application"
})
@EnableJpaRepositories(basePackages = "com.swa.order_infrastructure.order_dataaccess.repository")
@EntityScan(basePackages = "com.swa.order_infrastructure.order_dataaccess.entity")
@EnableFeignClients(basePackages = {
    "com.swa.order_infrastructure.order_external"
})
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
