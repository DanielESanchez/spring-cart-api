package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.models.Order;

import java.util.List;

public interface OrderService {
    Order findOrderById(String orderId, String header);
    Order findOrderById(String orderId);
    Order saveOrder(String orderUsername, String header);
    void buyOrder(String orderId, String header);
    List<Order> findOrdersByUsername(String username, String header);
    List<Order> findOrdersCompletedByUsername(String username, String header);
    List<Order> findOrdersCanceledByUsername(String username, String header);
    List<Order> findOrdersRefundedByUsername(String username, String header);
    List<Order> findAllOrdersCompleted();
    List<Order> findAllOrdersCanceled();
    List<Order> findAllOrdersRefunded();
    List<Order> findAllOrders();
    void cancelOrder(String orderId, String reason, String header);
    void refundOrder(String orderId);
    void completeOrder(String orderId);

}
