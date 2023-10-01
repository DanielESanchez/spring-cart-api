package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, ProductSearchRepository {
    @Query(value = "{productId:'?0', isEnable: true}")
    Optional<Product> findProductByProductId(String productId);

    @Query(value = "{productId:'?0'}")
    Optional<Product> findProductByProductIdAdmin(String productId);

    @Query(value = "{productId:'?0'}")
    Optional<Product> findProductToEnableDisable(String productId);

    @Query(value = "{categoriesId:'?0', isEnable: true}")
    List<Product> findProductsByCategoryId(String categoryId);

    @Query(value = "{isEnable: true}")
    List<Product> findAllUser();

}
