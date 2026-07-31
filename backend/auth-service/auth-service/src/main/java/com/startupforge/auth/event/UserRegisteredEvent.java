package com.startupforge.auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Published to RabbitMQ (exchange: user.events.exchange, routing key: user.registered)
 * whenever a new user successfully signs up. Consumed by Notification Service (Phase 3)
 * to send the OTP verification email. Auth Service does not know or care who consumes
 * this event or how the email is actually sent.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements Serializable {
    private UUID userId;
    private String name;
    private String email;
    private String otp;
}
