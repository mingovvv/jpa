package com.example.jpa.shop.domain.level2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private String id;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * N:1
     *   - 연관관계 주인
     *   - 하나의 맴버는 여러 주문을 할 수 있다.
     */
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    /**
     * 1:N
     *   - mappedBy 옵션으로 연관관계의 주인을 설정
     *   - 1:N에서 1은 read-only
     */
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 1:1
     */
    @OneToOne
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;


    /**
     * 연관관계 생성 메서드
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

}
