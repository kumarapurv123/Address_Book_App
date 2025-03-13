package com.example.addressbook.repository;


import com.example.addressbook.model.UserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository  extends CrudRepository<UserInfo, Long> {

    public UserInfo findByUsername(String username);
}