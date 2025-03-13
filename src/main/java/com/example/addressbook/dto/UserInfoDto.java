package com.example.addressbook.dto;


import com.example.addressbook.model.UserInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInfoDto  extends UserInfo {

    private String userName;

    private String lastName;
    private long phoneNumber;
    private String email;

}