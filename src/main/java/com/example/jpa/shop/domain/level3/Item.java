package com.example.jpa.shop.domain.level3;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
//@Inheritance(strategy = InheritanceType.JOINED) // 조인 전략
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 단일 테이블 전략
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // 구현 클래스마다 테이블 생성 전략
@DiscriminatorColumn
@Setter
@Getter
public abstract class Item extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private int price;

}
