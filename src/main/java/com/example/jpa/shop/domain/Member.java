package com.example.jpa.shop.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString(exclude = {"team"})
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    /**
     * @ManyToOne
     *   - Member N : Team 1 (N:1)
     *   - 하나의 팀은 여러 맴버를 가질 수 있다.
     *
     * @JoinColumn
     *   - RDBMS의 컬럼명 명시
     *   - 객체의 참조값을 RDBMS의 FK와 매핑
     */
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
//    private Long teamId;

}
