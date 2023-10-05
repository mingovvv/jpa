package com.example.jpa.shop.domain.level4;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Parent {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    /**
     * cascade : 영속성 전이 옵션 / child 객체의 영속화를 동시에 같이 수행됨
     * orphanRemoval : 고아객체 삭제 옵션 / 끊어진 자식들은 자동으로 delete 쿼리가 수행됨
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> childList = new ArrayList<>();

    /**
     * 연관관계 편의메서드
     */
    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }
}
