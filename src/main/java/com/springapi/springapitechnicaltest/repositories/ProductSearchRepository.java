package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Product;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;

public interface ProductSearchRepository {
    List<Product> findProductsByNameOrDescription(String text);
}
