package com.example.jpa.shop.domain.level2;

import javax.persistence.*;

@Entity
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "DELIVERY_ID")
    private Long id;

    private String city;

    private String street;

    private String zipcode;

    private DeliveryStatus status;

    /**
     * 1:1
     */
    @OneToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

}
