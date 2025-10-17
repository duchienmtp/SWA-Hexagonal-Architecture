package com.swa.notification_infrastructure.notification_messaging.config;


import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMQProperties {
    // These will be maps to hold the values from the YAML
    private Map<String, String> exchanges = new HashMap<>();
    private Map<String, String> queues = new HashMap<>();
    private Map<String, String> routingKeys = new HashMap<>();

    // Standard getters and setters
    public Map<String, String> getExchanges() { return exchanges; }
    public void setExchanges(Map<String, String> exchanges) { this.exchanges = exchanges; }
    public Map<String, String> getQueues() { return queues; }
    public void setQueues(Map<String, String> queues) { this.queues = queues; }
    public Map<String, String> getRoutingKeys() { return routingKeys; }
    public void setRoutingKeys(Map<String, String> routingKeys) { this.routingKeys = routingKeys; }
}
