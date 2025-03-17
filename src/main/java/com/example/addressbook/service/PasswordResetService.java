package com.example.addressbook.service;

import com.example.addressbook.dto.ChangePasswordDto;
import com.example.addressbook.dto.PasswordResetDto;
import com.example.addressbook.model.PasswordResetToken;
import com.example.addressbook.model.UserInfo;
import com.example.addressbook.repository.PasswordResetTokenRepository;
import com.example.addressbook.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    // You can inject EmailService to send the email
    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

//    @Transactional
//    public void initiatePasswordReset(String usernameOrEmail) {
//        UserInfo user = userRepository.findByUsername(usernameOrEmail);
//        if (user == null) {
//            throw new IllegalArgumentException("User not found with this username/email");
//        }
//        String token = generateSecureToken();
//        PasswordResetToken passwordResetToken = new PasswordResetToken();
//        passwordResetToken.setToken(token);
//        passwordResetToken.setUser(user);
//        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
//        tokenRepository.save(passwordResetToken);
//
//
//        System.out.println("Generated Password Reset Token: " + token);
//        // Send email to user with the token
//
//        String resetLink = "http://your-frontend-app/reset-password?token=" + token;
//        String emailText = "Click the following link to reset your password: " + resetLink;
//
//        emailService.sendSimpleMessage(user.getEmail(), "Password Reset Request", emailText);
//    }

    @Transactional
    public void initiatePasswordReset(String usernameOrEmail) {
        UserInfo user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found with this username/email");
        }
        String token = generateSecureToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
        tokenRepository.save(passwordResetToken);


        System.out.println("Generated Password Reset Token: " + token);
        // Send email to user with the token

        String resetLink = "http://your-frontend-app/reset-password?token=" + token;
        String emailText = "Click the following link to reset your password: " + resetLink;

        emailService.sendSimpleMessage(user.getEmail(), "Password Reset Request", emailText);
    }


    public void resetPassword(PasswordResetDto passwordResetDto) {
        PasswordResetToken token = tokenRepository.findByToken(passwordResetDto.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        UserInfo user = token.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
        userRepository.save(user);

        // Delete the used token (optional)
        tokenRepository.delete(token);
    }

    private String generateSecureToken() {
        return UUID.randomUUID().toString();
    }

    //change password
    public boolean changePassword(String username, ChangePasswordDto changePasswordDto) {
        UserInfo user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}