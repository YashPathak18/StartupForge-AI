package com.startupforge.auth.service;

import com.startupforge.auth.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.user-events-exchange}")
    private String userEventsExchange;

    @Value("${app.rabbitmq.user-registered-routing-key}")
    private String userRegisteredRoutingKey;

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("Publishing UserRegistered event for userId={}, email={}", event.getUserId(), event.getEmail());
        rabbitTemplate.convertAndSend(userEventsExchange, userRegisteredRoutingKey, event);
    }
}
