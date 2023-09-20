package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Product;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;
import java.util.Optional;

public interface ProductSearchRepository {
    List<Product> findProductsByNameOrDescription(String text, Optional<String> category);
}
