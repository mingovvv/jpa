package com.example.jpa.shop.domain.level4;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Child {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent parent;

    /**
     * 연관관계 편의메서드
     */
    public void addParent(Parent parent) {
        parent.getChildList().add(this);
        this.setParent(parent);
    }

}
