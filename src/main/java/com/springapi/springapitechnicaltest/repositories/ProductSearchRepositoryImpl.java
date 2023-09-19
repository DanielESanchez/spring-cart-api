package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

import java.util.List;

@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public List<Product> findProductsByNameOrDescription(String text) {
        var criteria = TextCriteria
                .forDefaultLanguage()
                .matchingAny(text);

        var query = TextQuery.queryText(criteria).sortByScore();

        return this.mongoTemplate.find(query, Product.class);
    }
}
