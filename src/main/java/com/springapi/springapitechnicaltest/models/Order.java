package com.springapi.springapitechnicaltest.models;

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

    private String username;

    private ShoppingCart shoppingCart;

    private Float total;

    @Value("${store.taxes}")
    private Integer taxValue;

    private Float taxes;

    private Date orderDate;

    private Boolean isPaid;

    private Date paymentDate;

    private Boolean isCompleted;

    private Date completedDate;

    private Boolean isCanceled;

    private Date cancelDate;

    private String cancelReason;

    private Boolean isRefunded;

    private Date refundDate;
}