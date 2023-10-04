package com.example.jpa.shop.domain.level2;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;


    /**
     * @ManyToMany
     *   - 실무에서 사용되지 않음
     *   - 1 : N / N : 1 로 풀어서 사용하자
     *   - 연관관계 팔로워
     */
    @ManyToMany(mappedBy = "items")
    private List<Category> items = new ArrayList<>();

}
