package com.example.addressbook.repository;


import com.example.addressbook.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository  extends CrudRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);
}