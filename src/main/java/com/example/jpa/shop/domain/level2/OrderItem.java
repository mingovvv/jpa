package com.example.jpa.shop.domain.level2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    /**
     * N:1
     */
    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    /**
     * N:1
     */
    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    private int orderPrice;

    private int count;

}
