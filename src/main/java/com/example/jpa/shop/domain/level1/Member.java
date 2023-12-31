package com.example.jpa.shop.domain.level1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    // 기간
//    private LocalDateTime startDate;
//    private LocalDateTime endDate;
    @Embedded
    private Period period;

    // 주소
//    private String city;
//    private String street;
//    private String zipcode;
    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "w_city")),
            @AttributeOverride(name = "street", column = @Column(name = "w_street")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "w_zipcode"))
    })
    private Address workAddress;

    /**
     * @ManyToOne
     *   - Member N : Team 1 (N:1)
     *   - 하나의 팀은 여러 맴버를 가질 수 있다.
     *   - fetch = FetchType
     *      - fetch = FetchType.LAZY    : 프록시 객체로 조회(실제 사용 시점 쿼리 나감)
     *      - fetch = FetchType.EAGER   : 실제 객체로 조회(join으로 한번에 가져옴)
     *
     * @JoinColumn
     *   - RDBMS의 컬럼명 명시
     *   - 객체의 참조값을 RDBMS의 FK와 매핑
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
//    private Long teamId;

}
