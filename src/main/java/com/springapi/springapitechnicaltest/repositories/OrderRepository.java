package com.springapi.springapitechnicaltest.repositories;

import com.springapi.springapitechnicaltest.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    @Query(value = "{ username: '?0' }")
    List<Order> findOrdersByUsername(String username);

    @Query(value = "{ username:  '?0', isCompleted: true}")
    List<Order> findOrderCompletedByUsername(String username);

    @Query(value = "{ username:  '?0', isCanceled: true}")
    List<Order> findOrderCanceledByUsername(String username);

    @Query(value = "{ username:  '?0', isRefunded: true}")
    List<Order> findOrderRefundedByUsername(String username);

    @Query( value = "{ isCompleted: true }")
    List<Order> findAllOrdersCompleted();

    @Query( value = "{ isCanceled: true }")
    List<Order> findAllOrdersCanceled();

    @Query( value = "{ isRefunded: true }")
    List<Order> findAllOrdersRefunded();
}
