package com.example.jpa.shop.domain.level2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;

    private String city;

    private String street;

    private String zipcode;

    /**
     * 1:N
     *   - mappedBy 옵션으로 연관관계의 주인을 설정
     *   - 1:N에서 1은 read-only
     */
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    /**
     * 연관관계 생성 메서드
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setMember(this);
    }

}
