package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {

    @Query(value = "{username: '?0'}")
    Optional<ShoppingCart> findShoppingCartByUsername(String username);
}
