package com.example.jpa.shop.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "members")
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    /**
     * @OneToMany
     *   - Team 1 : Member M (1:N)
     *   - 하나의 팀은 여러 맴버를 가질 수 있다.
     *   - [양방향 연관관계를 설정해주기 위한 필드]
     */
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

}
