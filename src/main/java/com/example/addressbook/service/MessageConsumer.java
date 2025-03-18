package com.example.addressbook.service;

import com.example.addressbook.dto.UserRegistrationMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.addressbook.config.RabbitMQConfig;

@Service
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private Gson gson;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        logger.info("Message received: {}", message);

        try {
            // Deserialize the JSON message
            UserRegistrationMessage userMessage = gson.fromJson(message, UserRegistrationMessage.class);

            logger.info("New user registered: Username={}, Email={}", userMessage.getUsername(), userMessage.getEmail());

            // Send welcome email
            String to = userMessage.getEmail();
            String subject = "Welcome to Addressbook!";
            String body = "Hi " + userMessage.getUsername() + ",\n\nWelcome to Addressbook! Your account has been created successfully.";

            emailService.sendSimpleMessage(to, subject, body);
        } catch (Exception ex) {
            logger.error("Error processing message: {} - {}", message, ex.getMessage(), ex);

        }
    }
}