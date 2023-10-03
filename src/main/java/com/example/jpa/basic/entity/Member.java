package com.example.jpa.basic.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
//@Table(name = "M_MEMBER")
@Getter @Setter

/**
 * 기본 키 매핑의
 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR") 와 함께 사용됨
 */
//@SequenceGenerator(
//        name = "MEMBER_SEQ_GENERATOR",
//        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
//        initialValue = 1, allocationSize = 1)

/**
 * 기본 키 매핑의
 * @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR") 와 함께 사용됨
 */
//@TableGenerator(
//        name = "MEMBER_SEQ_GENERATOR",
//        table = "MY_SEQUENCES",
//        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)
public class Member {

    /**
     * @Id : 직접 할당
     * @GeneratedValue : 자동 생성
     *   - strategy
     *      - GenerationType.IDENTITY : 기본 키 생성을 RDBMS에 위임(MySQL의 auto_increment 등..)
     *      - GenerationType.SEQUENCE : RDBMS 시퀀스 오브젝트 사용(Oracle)
     *      - GenerationType.TABLE    : 키 생성 전용 테이블을 만들어 시퀀스를 흉내내는 전략
     *      - GenerationType.AUTO (d) : DB에 따라 자동 선택됨
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column 속성
     *  - insertable : 등록 가능 여부 (d : true)
     *  - updatable : 수정 가능 여부 (d : true)
     *  - nullable[DDL] : not null 제약조건
     *  - unique[DDL] : unique 제약조건
     *  - length[DDL] : 문자 길이 제약조건
     *  - columnDefinition[DDL] : 컬럼 정보를 직접 입력
     */
    @Column(name = "name") // RDBMS 컬럼 이름과 다를 때
    private String username;

    private Integer age;

    /**
     * @Enumerated 속성
     *  - EnumType.ORDINAL : enum 순서를 DB에 저장 (default) / 사용하지 말 것
     *  - EnumType.STRING : enum 이름을 DB에 저장
     */
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    /**
     * LocalDate / LocalDateTime 날짜타입을 사용하는 경우 생략 가능
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob // varchar를 넘어서는 큰 크기의 텍스트
    private String description;

    @Transient // RDBMS와 매핑하지 않음
    private int temp;

}
