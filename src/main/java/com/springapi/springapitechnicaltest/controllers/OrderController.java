package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.models.Order;
import com.springapi.springapitechnicaltest.services.OrderService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
public class OrderController {
    private final OrderService orderService;

    @Value("${api.request.path}")
    private String PROPERTY_NAME;

    @ApiResponses( value = {
            @ApiResponse(responseCode = "201",
                    description = "Order saved successfully", headers = {
                    @Header(name = HttpHeaders.LOCATION, schema =
                    @Schema(type = "string"),
                            description = "Path of the order saved") },
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, product or shopping cart not found", content = { @Content(schema = @Schema) }),
    })
    @PostMapping("/order/new")
    ResponseEntity<HttpHeaders> saveOrder(@RequestBody Order order, @RequestHeader("Authorization") String header) {
        Order orderSaved = orderService.saveOrder(order, header);
        String location = PROPERTY_NAME + "/order/get/" + orderSaved.get_id();
        return ResponseEntity.created(URI.create(location)).build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Order marked as paid", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, order, product or shopping cart not found", content = { @Content(schema = @Schema) }),
    })
    @PatchMapping("/order/buy/{orderId}")
    ResponseEntity<?> buyOrder(@PathVariable String orderId, @RequestHeader("Authorization") String header){
        orderService.buyOrder(orderId, header);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Order marked as completed", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, order, product or shopping cart not found", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "409", description = "Order has another status that blocks this change", content = { @Content(schema = @Schema) })
    })
    @PatchMapping("/order/complete/{orderId}")
    ResponseEntity<?> completeOrder(@PathVariable String orderId){
        orderService.completeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Order marked as canceled", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, order, product or shopping cart not found", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "409", description = "Order has another status that blocks this change", content = { @Content(schema = @Schema) })
    })
    @PatchMapping("/order/cancel/{orderId}")
    ResponseEntity<?> cancelOrder(@PathVariable String orderId, @RequestHeader("Authorization") String header){
        orderService.cancelOrder(orderId, "Canceled by user", header);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "Order marked as refunded", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, order, product or shopping cart not found", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "409", description = "Order has another status that blocks this change", content = { @Content(schema = @Schema) })
    })
    @PatchMapping("/order/refund/{orderId}")
    ResponseEntity<?> refundOrder(@PathVariable String orderId){
        orderService.refundOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Order found", content = { @Content(schema = @Schema(implementation = Order.class)) }),
            @ApiResponse(responseCode = "404", description = "Order not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/order/get/{orderId}")
    Order getOrderById(@PathVariable String orderId, @RequestHeader("Authorization") String header){
        return orderService.findOrderById(orderId, header);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders for the username received"),
            @ApiResponse(responseCode = "403", description = "You do not have permission to see the orders for this username",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/orders/get/{username}")
    List<Order> getOrdersByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersByUsername(username, header);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders completed for the username received"),
            @ApiResponse(responseCode = "403", description = "You do not have permission to see the orders for this username",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/orders/get/completed/{username}")
    List<Order> getOrdersCompletedByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersCompletedByUsername(username, header);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders canceled for the username received"),
            @ApiResponse(responseCode = "403", description = "You do not have permission to see the orders for this username",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/orders/get/canceled/{username}")
    List<Order> getOrdersCanceledByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersCanceledByUsername(username, header);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders refunded for the username received"),
            @ApiResponse(responseCode = "403", description = "You do not have permission to see the orders for this username",
                    content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User, not found", content = { @Content(schema = @Schema) })
    })
    @GetMapping("/orders/get/refunded/{username}")
    List<Order> getOrdersRefundedByUsername(@PathVariable String username, @RequestHeader("Authorization") String header){
        return orderService.findOrdersRefundedByUsername(username, header);
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders saved in the database")
    })
    @GetMapping("/orders/all/get")
    List<Order> getAllOrders(){
        return orderService.findAllOrders();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders completed saved in the database"),
    })
    @GetMapping("/orders/all/get/completed")
    List<Order> getAllOrdersCompleted(){
        return orderService.findAllOrdersCompleted();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders canceled saved in the database")
    })
    @GetMapping("/orders/all/get/canceled")
    List<Order> getAllOrdersCanceled(){
        return orderService.findAllOrdersCanceled();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",
                    description = "Showing all orders refunded saved in the database")
    })
    @GetMapping("/orders/all/get/refunded")
    List<Order> getAllOrdersRefunded(){
        return orderService.findAllOrdersRefunded();
    }
}
