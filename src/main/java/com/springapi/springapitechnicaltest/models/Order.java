package com.springapi.springapitechnicaltest.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("orders")
public class Order {
    @Id
    private String _id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    private ShoppingCart shoppingCart;

    private Float total;

    @Value("${store.taxes}")
    private Integer taxValue;

    private Float taxes;

    private Date orderDate;

    private Boolean isPaid = false;

    private Date paymentDate;

    private Boolean isCompleted = false;

    private Date completedDate;

    private Boolean isCanceled = false;

    private Date cancelDate;

    private String cancelReason;

    private Boolean isRefunded = false;

    private Date refundDate;
}