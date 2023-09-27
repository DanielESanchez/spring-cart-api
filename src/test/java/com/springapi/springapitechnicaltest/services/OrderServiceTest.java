package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.ConflictException;
import com.springapi.springapitechnicaltest.controllers.ForbiddenException;
import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OrderServiceTest {
    @Autowired
    OrderServiceImpl orderService;
    @MockBean
    OrderRepository orderRepository;
    @MockBean
    UserService userService;
    @MockBean
    JwtService jwtService;
    @MockBean
    ShoppingCartService shoppingCartService;
    private Order order;
    private List<Order> orders;
    private Order orderDifferentUser;
    private final String orderId = "1";
    private final String header = "Bearer token123152154d2sa2dsa";
    private final UserRole userRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build();
    private final UserRole adminRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_ADMIN).build()).build();
    private User testUser;
    private User testUserDifferent;
    private User testAdmin;
    private ShoppingCart shoppingCart;

    @BeforeEach
    public void setUp() {
        order = new Order();
        order.set_id(orderId);
        order.setUsername("user");

        Order orderPaid = order;
        orderPaid.setIsPaid(true);
        orderPaid.setOrderDate(new Date());

        orderDifferentUser = new Order();
        orderDifferentUser.set_id("2");
        orderDifferentUser.setUsername("unrealUser");

        Set<UserRole> rolesUser = new HashSet<>();
        rolesUser.add(userRole);

        Set<UserRole> rolesAdmin = new HashSet<>();
        rolesAdmin.add(userRole);
        rolesAdmin.add(adminRole);

        testUser = new User();
        testUser.setUsername("user");
        testUser.setUserRoles(rolesUser);

        testUserDifferent = new User();
        testUserDifferent.setUsername("userDifferent");
        testUserDifferent.setUserRoles(rolesUser);

        testAdmin = new User();
        testAdmin.setUsername("admin");
        testAdmin.setUserRoles(rolesAdmin);

        shoppingCart = new ShoppingCart();
        shoppingCart.setTotal( (float) 69.0 );

        orders = new ArrayList<>();
        orders.add(order);
        orders.add(orderPaid);

    }


    @Test
    void shouldReturnOrder_WhenFindOrderByIdAsSameUserThanUsername() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.findOrderById(orderId, header);

        assertEquals(order, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldReturnForbidden_WhenFindOrderByIdAsDifferentUser() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDifferentUser));

        assertThrows(ForbiddenException.class, ()-> orderService.findOrderById(orderId, header));
    }

    @Test
    void shouldReturnOrder_WhenFindOrderByIdAsAdmin() {
        when(jwtService.extractUsername(anyString())).thenReturn(testAdmin.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testAdmin);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDifferentUser));

        Order result = orderService.findOrderById(orderId);

        assertEquals(orderDifferentUser, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldReturnOrder_WhenSaveOrder() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(shoppingCartService.findShoppingCartByUsername(anyString())).thenReturn(shoppingCart);
        when(orderRepository.save(order)).thenReturn(orderDifferentUser);

        Order result = orderService.saveOrder(order, header);

        assertEquals(result, orderDifferentUser);
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnForbidden_WhenSaveOrderDifferentUserAsUsernameFromOrder() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(shoppingCartService.findShoppingCartByUsername(anyString())).thenReturn(shoppingCart);
        when(orderRepository.save(order)).thenReturn(orderDifferentUser);

        assertThrows(ForbiddenException.class, ()-> orderService.saveOrder(orderDifferentUser, header));

    }

    @Test
    void shouldReturnNothing_WhenBuyOrderAsUserFromUsernameInOrder() {
        order.setIsPaid(false);

        when(orderRepository.findById(anyString())).thenReturn(java.util.Optional.of(order));
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);

        orderService.buyOrder(order.get_id(), header);

        verify(orderRepository, times(1)).save(order);

        assertNotNull(order.getPaymentDate());
        assertTrue(order.getIsPaid());
    }

    @Test
    void shouldReturnForbidden_WhenBuyOrderDifferentUserThanUsernameInOrder() {
        orderDifferentUser.setIsPaid(false);

        when(orderRepository.findById(anyString())).thenReturn(java.util.Optional.of(orderDifferentUser));
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);

        assertThrows(ForbiddenException.class, ()-> orderService.buyOrder(order.get_id(), header));
        verify(orderRepository, times(0)).save(order);
    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersByUsernameAsSameUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrdersByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersByUsername(testUser.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnForbidden_WhenFindOrdersByUsernameAsDifferentUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrdersByUsername(anyString())).thenReturn(orders);

        assertThrows(ForbiddenException.class, ()-> orderService.findOrdersByUsername("unrealUser", header));
        verify(orderRepository, times(0)).findOrdersByUsername(anyString());
    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersCompletedByUsernameAsSameUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrderCompletedByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersCompletedByUsername(testUser.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersCompletedByUsernameAsAdminLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testAdmin);
        when(orderRepository.findOrderCompletedByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersCompletedByUsername(testAdmin.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnForbidden_WhenFindOrdersCompletedByUsernameAsDifferentUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrderCompletedByUsername(anyString())).thenReturn(orders);

        assertThrows(ForbiddenException.class, ()-> orderService.findOrdersCompletedByUsername("unrealUser", header));
        verify(orderRepository, times(0)).findOrdersByUsername(anyString());
    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersCanceledByUsernameAsSameUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrderCanceledByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersCanceledByUsername(testUser.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersCanceledByUsernameAsAdminLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testAdmin);
        when(orderRepository.findOrderCanceledByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersCanceledByUsername(testUser.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    void shouldReturnForbidden_WhenFindOrdersCanceledByUsernameAsDifferentUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrderCanceledByUsername(anyString())).thenReturn(orders);

        assertThrows(ForbiddenException.class, ()-> orderService.findOrdersCanceledByUsername("unrealUser", header));
        verify(orderRepository, times(0)).findOrdersByUsername(anyString());

    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersRefundedByUsernameAsSameUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrderRefundedByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersRefundedByUsername(testUser.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOrderList_WhenFindOrdersRefundedByUsernameAsAdminLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testAdmin.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testAdmin);
        when(orderRepository.findOrderCanceledByUsername(anyString())).thenReturn(orders);

        List<Order> result = orderService.findOrdersCanceledByUsername(testUser.getUsername(), header);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnForbidden_WhenFindOrdersRefundedByUsernameAsDifferentUserLoggedIn() {
        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findOrderRefundedByUsername(anyString())).thenReturn(orders);

        assertThrows(ForbiddenException.class, ()-> orderService.findOrdersRefundedByUsername("unrealUser", header));
        verify(orderRepository, times(0)).findOrdersByUsername(anyString());
    }

    @Test
    void shouldReturnOrderList_WhenFindAllOrdersCompleted() {
        when(orderRepository.findAllOrdersCompleted()).thenReturn(orders);

        List<Order> result = orderService.findAllOrdersCompleted();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAllOrdersCompleted();
    }

    @Test
    void shouldReturnOrderList_WhenFindAllOrdersCanceled() {
        when(orderRepository.findAllOrdersCanceled()).thenReturn(orders);

        List<Order> result = orderService.findAllOrdersCanceled();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAllOrdersCanceled();
    }

    @Test
    void shouldReturnOrderList_WhenFindAllOrdersRefunded() {
        when(orderRepository.findAllOrdersRefunded()).thenReturn(orders);

        List<Order> result = orderService.findAllOrdersRefunded();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAllOrdersRefunded();
    }

    @Test
    void shouldReturnOrderList_WhenFindAllOrders() {
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.findAllOrders();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnNothing_WhenCancelOrderAsSameUserLoggedIn() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);
        String reason = "Canceled by user";

        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(orderId, reason, header);

        assertTrue(order.getIsCanceled());
        assertNotNull(order.getCancelDate());
        assertEquals(order.getCancelReason(), reason);
    }

    @Test
    void shouldReturnConflictException_WhenCancelOrderCompleted() {
        order.setIsCanceled(false);
        order.setIsCompleted(true);
        String reason = "Canceled by user";

        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUser);
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(ConflictException.class, ()-> orderService.cancelOrder(orderId, reason, header));
        verify(orderRepository, times(0)).save(order);
    }

    @Test
    void shouldReturnNothing_WhenCancelOrderAsAdmin() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);
        String reason = "Canceled by user";

        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testAdmin);
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(orderId, reason, header);

        assertTrue(order.getIsCanceled());
        assertNotNull(order.getCancelDate());
        assertEquals(order.getCancelReason(), "Cancelled by admin");
    }

    @Test
    void shouldReturnForbidden_WhenCancelOrderAsDifferentUser() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);
        String reason = "Canceled by user";

        when(jwtService.extractUsername(anyString())).thenReturn(testUser.getUsername());
        when(userService.findUserByUsername(anyString())).thenReturn(testUserDifferent);
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(ForbiddenException.class, ()-> orderService.cancelOrder(orderId, reason, header));
        verify(orderRepository, times(0)).save(any(Order.class));
    }

    @Test
    void shouldReturnNothing_WhenRefundOrder() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);

        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.refundOrder(orderId);

        assertTrue(order.getIsRefunded());
        assertNotNull(order.getRefundDate());
    }

    @Test
    void shouldReturnConflictException_WhenRefundOrderNotPaid() {
        System.out.println(order.getIsPaid());
        order.setIsCanceled(false);
        order.setIsCompleted(false);
        order.setIsPaid(false);

        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(ConflictException.class, ()-> orderService.refundOrder(orderId));
        verify(orderRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnNothing_WhenCompleteOrder() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);

        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.completeOrder(orderId);

        assertTrue(order.getIsCompleted());
        assertNotNull(order.getCompletedDate());
    }

    @Test
    void shouldReturnConflictException_WhenCompleteOrderNotPaid() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);
        order.setIsPaid(false);

        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(ConflictException.class, ()-> orderService.completeOrder(orderId));
        verify(orderRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnConflictException_WhenCompleteOrderCancelled() {
        order.setIsCanceled(true);
        order.setIsCompleted(false);
        order.setIsPaid(true);

        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(ConflictException.class, ()-> orderService.completeOrder(orderId));
        verify(orderRepository, times(0)).save(any());
    }

    @Test
    void shouldReturnConflictException_WhenCompleteOrderRefunded() {
        order.setIsCanceled(false);
        order.setIsCompleted(false);
        order.setIsRefunded(true);
        order.setIsPaid(true);

        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertThrows(ConflictException.class, ()-> orderService.completeOrder(orderId));
        verify(orderRepository, times(0)).save(any());
    }
}