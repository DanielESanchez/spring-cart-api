package com.springapi.springapitechnicaltest.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.springapi.springapitechnicaltest.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    @Query(value = "{username:'?0'}")
    Optional<User> findUserByUsername(String username);

}