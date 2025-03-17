package com.example.addressbook.service;



import com.example.addressbook.dto.UserInfoDto;
import com.example.addressbook.dto.UserRegistrationMessage;
import com.example.addressbook.model.UserInfo;
import com.example.addressbook.repository.UserRepository;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private Gson gson;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByUsername(username);
        if(user == null){

            throw new UsernameNotFoundException("could not found user..!!");
        }

        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public Boolean signupUser(UserInfoDto userInfoDto){
        //        ValidationUtil.validateUserAttributes(userInfoDto);
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(),userInfoDto.getEmail(), new HashSet<>()));
//        messageProducer.sendMessage("New user registered: " + userInfoDto.getUsername());
// After successful signup, create and send the message object
        UserRegistrationMessage message = new UserRegistrationMessage();
        message.setUsername(userInfoDto.getUsername());
        message.setEmail(userInfoDto.getEmail());
        // Set other fields

        messageProducer.sendMessage(gson.toJson(message)); // Send JSON string

        return true;
    }
}