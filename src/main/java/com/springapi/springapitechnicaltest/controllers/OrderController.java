package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Order;
import com.springapi.springapitechnicaltest.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/${api.request.path}")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Value("${api.request.path}")
    private String PROPERTY_NAME;
    @PostMapping("/order/new")
    ResponseEntity<HttpHeaders> saveOrder(@RequestBody Order order, @RequestHeader("Authorization") String header) {
        Order orderSaved = orderService.saveOrder(order, header);
        String location = PROPERTY_NAME + "/order/get/" + orderSaved.get_id();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @PatchMapping("/order/buy/{orderId}")
    ResponseEntity<?> buyOrder(@PathVariable String orderId, @RequestHeader("Authorization") String header){
        orderService.buyOrder(orderId, header);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/order/complete/{orderId}")
    ResponseEntity<?> completeOrder(@PathVariable String orderId){
        orderService.completeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/order/cancel/{orderId}")
    ResponseEntity<?> cancelOrder(@PathVariable String orderId, @RequestHeader("Authorization") String header){
        orderService.cancelOrder(orderId, "Canceled by user", header);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/order/refund/{orderId}")
    ResponseEntity<?> refundOrder(@PathVariable String orderId){
        orderService.refundOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/get/{orderId}")
    Order getOrderById(@PathVariable String orderId){
        return orderService.findOrderById(orderId);
    }

    @GetMapping("/order/get/{username}")
    List<Order> getOrdersByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersByUsername(username, header);
    }

    @GetMapping("/order/get/completed/{username}")
    List<Order> getOrdersCompletedByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersCompletedByUsername(username, header);
    }

    @GetMapping("/order/get/canceled/{username}")
    List<Order> getOrdersCanceledByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersCanceledByUsername(username, header);
    }

    @GetMapping("/order/get/refunded/{username}")
    List<Order> getOrdersRefundedByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersRefundedByUsername(username, header);
    }

    @GetMapping("/orders/get")
    List<Order> getAllOrders(){
        return orderService.findAllOrders();
    }

    @GetMapping("/orders/get/completed")
    List<Order> getAllOrdersCompleted(){
        return orderService.findAllOrdersCompleted();
    }

    @GetMapping("/orders/get/canceled")
    List<Order> getAllOrdersCanceled(){
        return orderService.findAllOrdersCanceled();
    }

    @GetMapping("/orders/get/refunded")
    List<Order> getAllOrdersRefunded(){
        return orderService.findAllOrdersRefunded();
    }
}
