package com.swa.notification_infrastructure.notification_messaging.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swa.notification_domain.event.OrderConfirmationEvent;

@Configuration
public class RabbitMQJsonConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Create a map to tell the converter which class to use
        // for a given __TypeId__ header.
        Map<String, Class<?>> idMappings = new HashMap<>();

        // Key: The __TypeId__ from the producer (from your stack trace)
        // Value: The local class you want to deserialize it into
        idMappings.put("com.swa.order_domain.event.OrderConfirmationEvent",
                OrderConfirmationEvent.class);

        // Use DefaultClassMapper to supply id->class mappings and set it on the converter
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idMappings);
        converter.setClassMapper(classMapper);

        return converter;
    }
}
