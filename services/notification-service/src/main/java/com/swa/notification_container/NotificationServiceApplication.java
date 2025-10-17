package com.swa.notification_container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.swa.notification_container",
		"com.swa.notification_domain",
		"com.swa.notification_infrastructure",
		"com.swa.notification_application"
})
@EnableMongoRepositories(basePackages = "com.swa.notification_infrastructure.notification_dataaccess.repository")
@EntityScan(basePackages = "com.swa.notification_infrastructure.notification_dataaccess.entity")
@EnableKafka
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
