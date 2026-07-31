package com.startupforge.auth.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auth Service only PUBLISHES to this exchange — it does not declare or consume
 * from any queue itself. The queue below exists so the exchange/binding topology
 * is fully declared from this service (useful when running it standalone before
 * Notification Service exists). Notification Service (Phase 3) will declare its
 * own consumer-side binding to the same exchange/routing key independently.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.user-events-exchange}")
    private String userEventsExchange;

    @Value("${app.rabbitmq.user-registered-routing-key}")
    private String userRegisteredRoutingKey;

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange, true, false);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue("user.registered.queue", true);
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder
                .bind(userRegisteredQueue())
                .to(userEventsExchange())
                .with(userRegisteredRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        // JacksonJsonMessageConverter (Jackson 3), not the deprecated-for-removal
        // Jackson2JsonMessageConverter - matches Spring Boot 4's Jackson 3 default,
        // consumed on the other side by Notification Service (Phase 3), which
        // must use the same Jackson-3-based converter to deserialize this event.
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
