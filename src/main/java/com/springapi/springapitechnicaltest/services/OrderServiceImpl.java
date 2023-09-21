package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.ConflictException;
import com.springapi.springapitechnicaltest.controllers.ForbiddenException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Order;
import com.springapi.springapitechnicaltest.models.ShoppingCart;
import com.springapi.springapitechnicaltest.models.User;
import com.springapi.springapitechnicaltest.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final JwtService jwtService;

    @Value("${store.taxes}")
    private Integer tax;

    @Override
    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow( ()-> new NotFoundException("The order: '" + orderId + "could not be found."));
    }

    @Override
    public Order saveOrder(Order newOrder, String header) {
        userService.findUserByUsername(newOrder.getUsername());
        checkUser(header, newOrder.getUsername());
        newOrder.setShoppingCart(shoppingCartService.findShoppingCartByUsername(newOrder.getUsername()) );
        newOrder.setOrderDate(new Date());
        newOrder.setTaxes(calculateTaxes(newOrder.getShoppingCart()));
        newOrder.setTotal(newOrder.getTaxes() + newOrder.getShoppingCart().getTotal());
        return orderRepository.save(newOrder);
    }

    @Override
    public void buyOrder(String orderId, String header) {
        Order orderFound = findOrderById(orderId);
        checkUser(header, orderFound.getUsername());
        orderFound.setIsPaid(true);
        orderFound.setPaymentDate(new Date());
        orderRepository.save(orderFound);
    }

    @Override
    public List<Order> findOrdersByUsername(String username, String header) {
        userService.findUserByUsername(username);
        checkUser(header, username);
        return orderRepository.findOrdersByUsername(username);
    }

    @Override
    public List<Order> findOrdersCompletedByUsername(String username, String header) {
        checkUser(header, username);
        return orderRepository.findOrderCompletedByUsername(username);
    }

    @Override
    public List<Order> findOrdersCanceledByUsername(String username, String header) {
        checkUser(header, username);
        return orderRepository.findOrderCanceledByUsername(username);
    }

    @Override
    public List<Order> findOrdersRefundedByUsername(String username, String header) {
        checkUser(header, username);
        return orderRepository.findOrderRefundedByUsername(username);
    }

    @Override
    public List<Order> findAllOrdersCompleted() {
        return orderRepository.findAllOrdersCompleted();
    }

    @Override
    public List<Order> findAllOrdersCanceled() {
        return orderRepository.findAllOrdersCanceled();
    }

    @Override
    public List<Order> findAllOrdersRefunded() {
        return orderRepository.findAllOrdersRefunded();
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void cancelOrder(String orderId, String reason, String header) {
        Order orderFound = findOrderById(orderId);
        checkUser(header, orderFound.getUsername());
        if(orderFound.getIsCompleted()) throw new ConflictException("This order has been completed and cannot be canceled");
        orderFound.setIsCanceled(true);
        orderFound.setCancelDate(new Date());
        orderFound.setCancelReason(reason);
        orderRepository.save(orderFound);
    }

    @Override
    public void refundOrder(String orderId) {
        Order orderFound = findOrderById(orderId);
        orderFound.setIsRefunded(true);
        orderFound.setRefundDate(new Date());
        orderRepository.save(orderFound);
    }

    @Override
    public void completeOrder(String orderId) {
        Order orderFound = findOrderById(orderId);
        if(orderFound.getIsCanceled()) throw new ConflictException("This order has been canceled before complete");
        if(orderFound.getIsRefunded()) throw new ConflictException("This order has been refunded before complete");
        orderFound.setIsCompleted(true);
        orderFound.setCompletedDate(new Date());
        orderRepository.save(orderFound);
    }

    private Float calculateTaxes(ShoppingCart shoppingCart){
        return shoppingCart.getTotal() * tax;
    }

    private void checkUser(String header, String usernameToCheck){
        final String jwt = header.substring(7);
        final String username = jwtService.extractUsername(jwt);
        User user = userService.findUserByUsername(username);
        if(!user.hasRole("ADMIN") || !user.getUsername().equals(usernameToCheck)) {
            throw new ForbiddenException("This user cannot complete this process");
        }
    }
}
