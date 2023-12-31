package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.ConflictException;
import com.springapi.springapitechnicaltest.controllers.ForbiddenException;
import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.repositories.OrderRepository;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final JwtService jwtService;
    private final ProductService productService;

    @Value("${store.taxes}")
    private Integer tax;

    @Override
    public Order findOrderById(String orderId,  String header) {
        Order orderFound = orderRepository.findById(orderId)
                .orElseThrow( ()-> new NotFoundException("The order: '" + orderId + "' could not be found."));
        checkUser(header, orderFound.getUsername());
        return orderFound;
    }

    @Override
    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow( ()-> new NotFoundException("The order: '" + orderId + "' could not be found."));
    }

    @Override
    public Order saveOrder(String orderUsername, String header) {
        userService.findUserByUsername(orderUsername);
        Order newOrder = new Order();
        checkUser(header, orderUsername);
        ShoppingCart cartFound = shoppingCartService.findShoppingCartByUsername(orderUsername);
        if(cartFound.getProducts().size() < 1 ) throw new ConflictException("No products saved in the shopping cart");
        newOrder.setShoppingCart(cartFound );
        newOrder.setUsername(orderUsername);
        newOrder.setOrderDate(new Date());
        newOrder.setTaxes(calculateTaxes(newOrder.getShoppingCart()));
        newOrder.setTotal(newOrder.getTaxes() + newOrder.getShoppingCart().getTotal());
        log.info(new Date() + " New order saved for user " + orderUsername);
        return orderRepository.save(newOrder);
    }

    @Override
    public void buyOrder(String orderId, String header) {
        Order orderFound = findOrderById(orderId);
        checkUser(header, orderFound.getUsername());
        if(orderFound.getShoppingCart() == null || orderFound.getShoppingCart().getProducts().size() < 1)
            throw new ConflictException("Cannot buy an empty shopping cart");
        orderFound.setIsPaid(true);
        orderFound.setPaymentDate(new Date());
        for (ProductShoppingCart product: orderFound.getShoppingCart().getProducts()) {
            Product productFound = productService.getProductByProductId(product.getProductId());
            productFound.setQuantityAvailable(productFound.getQuantityAvailable() - product.getQuantity());
            productService.updateProduct(productFound);
        }
        log.info(new Date() + " The user " + orderFound.getUsername() +" has paid the order: " + orderId);
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
        User userFound = checkUser(header, orderFound.getUsername());
        if(userFound.hasRole("ADMIN")) reason = "Cancelled by admin";
        if(orderFound.getIsCompleted()) throw new ConflictException("This order has been completed and cannot be canceled");
        orderFound.setIsCanceled(true);
        orderFound.setCancelDate(new Date());
        orderFound.setCancelReason(reason);
        log.info(new Date() + " The order " + orderId + " has been canceled by user " + userFound.getUsername());
        orderRepository.save(orderFound);
    }

    @Override
    public void refundOrder(String orderId) {
        Order orderFound = findOrderById(orderId);
        if(!orderFound.getIsPaid()) throw new ConflictException("This order has not been paid.");
        orderFound.setIsRefunded(true);
        orderFound.setRefundDate(new Date());
        log.info(new Date() + " The order " + orderId + " has been refunded ");
        orderRepository.save(orderFound);
    }

    @Override
    public void completeOrder(String orderId) {
        Order orderFound = findOrderById(orderId);
        if(!orderFound.getIsPaid()) throw new ConflictException("This order has not been paid.");
        if(orderFound.getIsCanceled()) throw new ConflictException("This order has been canceled before complete");
        if(orderFound.getIsRefunded()) throw new ConflictException("This order has been refunded before complete");
        orderFound.setIsCompleted(true);
        orderFound.setCompletedDate(new Date());
        log.info(new Date() + " The order " + orderId + " has been marked as completed ");
        orderRepository.save(orderFound);
    }

    private Float calculateTaxes(ShoppingCart shoppingCart){
        return shoppingCart.getTotal() * ( (float) tax / (float) 100 );
    }

    private User checkUser(String header, String usernameToCheck){
        final String jwt = header.substring(7);
        final String username = jwtService.extractUsername(jwt);
        User user = userService.findUserByUsername(username);
        if(!user.hasRole("ADMIN") && !user.getUsername().equals(usernameToCheck)) {
            throw new ForbiddenException("This user cannot complete this process");
        }
        return user;
    }
}
