package com.springapi.springapitechnicaltest.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.springapi.springapitechnicaltest.models.UserModel;
import org.springframework.data.mongodb.repository.Query;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {

    @Query(value = "{username:'?0'}")
    UserModel findUserByUsername(String username);

}