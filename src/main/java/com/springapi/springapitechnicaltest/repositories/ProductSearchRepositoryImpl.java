package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public List<Product> findProductsByNameOrDescription(String text, Optional<String> category) {
        TextCriteria criteria = TextCriteria
                .forDefaultLanguage()
                .matchingAny(text);
        Query query;
        if (category.isPresent() && !category.get().isEmpty()) {
            query = Query.query(Criteria.where("isEnable").is(true))
                    .addCriteria(Criteria.where("categoriesId").is(category.get()))
                    .addCriteria(criteria);
        }else {
            query = Query.query(Criteria.where("isEnable").is(true))
                .addCriteria(criteria);
        }

        return this.mongoTemplate.find(query, Product.class);
    }
}
