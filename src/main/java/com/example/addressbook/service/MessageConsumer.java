package com.example.addressbook.service;

import com.example.addressbook.dto.UserRegistrationMessage;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.addressbook.config.RabbitMQConfig;

@Service
public class MessageConsumer {

    @Autowired
    private EmailService emailService;

    @Autowired
    private Gson gson;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("Message received: " + message);

        // Deserialize the JSON message
        UserRegistrationMessage userMessage = gson.fromJson(message, UserRegistrationMessage.class);

        System.out.println("New user registered:");
        System.out.println("Username: " + userMessage.getUsername());
        System.out.println("Email: " + userMessage.getEmail());

        // Send welcome email
        String to = userMessage.getEmail();
        String subject = "Welcome to Addressbook!";
        String body = "Hi " + userMessage.getUsername() + ",\n\nWelcome to Addressbook! Your account has been created successfully.";

        emailService.sendSimpleMessage(to, subject, body);
    }
}