# JPA

----
## JPA
- Java Persistence API
- Java 진영의 ORM 기술 표준
  - Object-Relational Mapping
  - 객체 <-> RDBMS 매핑
- 내부적으로 JDBC API를 사용하여 RDBMS와 교류

## JPA 사용하는 이유
- SQL 중심적인 개발 -> 객체 중심적인 개발
- 생산성 증대 및 유지보수
- `객체와 RMDBS의 패러다임`의 불일치 해결
- JPA의 성능최적화 기능
  - 1차 개시와 동일성 보장
    - 같은 트랙잰션 안에서는 같은 엔티티를 반환
    ```java
    Long orderId = 1107;
    Order o1 = jpa.find(Order.class, orderId); // SQL 전송
    Order o2 = jpa.find(Order.class, orderId); // caching
    // o1 == o2 true
    ```
  - 트랙잭션을 지원하는 쓰기 지연
    - 커밋 직전까지 `insert sql`를 모아두었다가 JDBC Batch SQL 기능을 사용해서 한번에 SQL 전송
    - 네트워크 통신비용이 줄어듦
  - 지연 로딩(Lazy Loading)
    ![Desktop View](/images/1.png)

## JPA 패러다임의 불일치 해결
- `OrderMenu` 객체는 `Order`를 상속받은 상태 
1. JPA와 상속
    ```java
    // jpa
    jpa.persist(orderMenu);
    
    // query
    insert into ORDER ...;
    insert into ORDER_MENU ...;
    ```
    ```java
    // jpa
    jpa.find(OrderMenu.class, orderMenuId);
    
    // query
    select o.*, om.*
    form ORDER o
    LEFT JOIN ORDER_MENU om 
    ON o.order_menu_id = om.order_menu_id
    ```
2. JPA와 연관관계
   ```java
   order.setOrderMenu(orderMenu);
   jpa.persist(order);
   ```
3. JPA와 객체 그래프 탐색
   ```java
   Order o = jpa.find(Order.class, orderId);
   OrderMenu om = o.getOrderMenu();
   ```

----

## 영속성 컨텍스트
 `EntityManager`를 통해 영속성 컨텍스트에 접근. 엔티티를 영구 저장하는 환경

#### 영속성 컨텍스트 생명주기
![Desktop View](/images/2.png)
- 비영속
  - 영속성 컨텍스트와 관계가 없는 새로운 상태
    ![Desktop View](/images/3.png)

- 영속
  - 1차 캐시에 올라간 상태 
  - 영속성 컨텍스트로부터 관리되는 상태
    ![Desktop View](/images/4.png)

- 준영속
  - `em.clear();` / `em.detach(entity);` / `em.close();` 
  - 영속성 컨텍스트에 저장되었다가 분리된 상태
    ![Desktop View](/images/5.png)

- 삭제
  - 삭제된 상태

#### 영속성 컨텍스트의 장점
1. 1차캐시
  ![Desktop View](/images/6.png)
    - 영속화 상태가 된 객체를 조회할 경우, 1차 캐시에 해당 객체가 있는지 조회
    - persist()로 객체를 영속화할 때, 이미 1차 캐시에 들어가기 때문에 조회 시, select 문이 발생하지 않음
    - `EntityManager`는 트랙잭션과 생명주기가 같으므로 성능에 엄청한 효과가 있는 것은 아님
2. 쓰기 지연
  ![Desktop View](/images/7.png) 
    ```java
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    
    // memberA 영속화
    em.persist(memberA);
    // memberB 영속화
    em.persist(memberB);
   
    // 커밋시점에 memberA, memberB Insert SQL가 생성되어 DB와 통신
    tx.commit(); 
    ``` 
3. dirty-checking
   ![Desktop View](/images/7.png)
    - `스냅샷`을 바탕으로 객체의 변경사항을 감지하여 업데이트 쿼리를 생성
    ```java
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    
    // 조회
    Member m = em.find(Member.class, 1L);
   
    m.setName("mingo");
    
    // 커밋시점에 Update SQL가 생성되어 DB와 통신
    tx.commit(); 
    ``` 

#### 플러시(flush)
- 영속성 컨텍스트의 변경내용(등록, 수정, 삭제)을 DB에 반영
- 쓰기지연 저장소에 쌓아놨던 SQL이 DB에 호출되는 시점
- `영속성 컨텍스트와 DB의 싱크를 맞추는 역할`
- flush와 1차 캐시 초기화는 관계없음
- 플러시 동작 : `em.flush();` / `tx.commit();` / JPQL query 실행 시
  - JPQL query 실행 시? 
  ```java
  // A, B, C는 쓰기지연 저장소와 1차캐시에서 관리되는 상태
  // 이 데이터는 트랙잭션이 커밋되는 시점에 DB와 통신이 될 예정
  em.persist(A);
  em.persist(B);
  em.persist(C);
  
  // JPQL을 통해서 조회를 하네? -> 원래 동작이라면 실제 DB와 통신되기 전이라 조회되는 데이터가 없음
  // 이런 사항을 방지하고자 JPA는 JPQL을 사용하면 flush()를 내부에서 미리 호출함 
  q = em.createQuery("select m from Member m", Member.class);
  List<Member> m = q.getResultList();
  ```
  
-----
## 엔티티 
[엔티티 매핑 속성](src/main/java/com/example/jpa/basic/entity/Member.java)

#### GenerationType.IDENTITY 전략
```java
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```
기본 키 매핑 전략 중에서 `@GeneratedValue(strategy = GenerationType.IDENTITY)` 를
사용하면 실제 RDBMS를 통해 `insert sql`이 실행되고 나서야 `id`를 확인할 수 있다.

하지만 `영속성 컨텍스트`에서 데이터가 관리되려면 `id` 값이 필수적으로 필요하다.

JPA에서는 `GenerationType.IDENTITY` 전략에서만 예외적으로 커밋 이전에 SQL를 실행시켜 해결하였다.
```java
Member member = new Member();
member.setName("member");
em.persist(member); // SQL 쿼리 실행 시점
/**
 insert into
 Member
 (id, age, createdDate, description, lastModifiedDate, roleType, name) 
 values
 (default, ?, ?, ?, ?, ?, ?)
 */
// insert 쿼리가 실행되고 해당 데이터를 select 해온 뒤에 1차 캐시에 저장해둔다.
System.out.println(member.getId()); // '1' 출력 됨 (DB에서 select 해왔기 때문)
tx.commit();
```

#### GenerationType.SEQUENCE 전략

```java
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 50) // allocationSize 성능 최적화
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
}
```
`GenerationType.SEQUENCE` 전략에서 `allocationSize` 옵션을 통해 네트워크 통신비용을 줄일 수 있다.
`allocationSize` 옵션은 default 50으로 설정되어 있고 한번에 시퀀스 50개를 로컬로 가져와서 하나씩 소비하는 방식이다.
동시성 이슈없이 최적화된 옵션이므로 오라클 DB를 사용하면 유용하게 사용할 수 있음

-----

## 연관관계 매핑
> 객체와 테이블 연관관계의 차이를 이해하고 객체의 `참조`와 테이블의 `FK`를 매핑하는 것이 핵심

#### 객체를 테이블에 맞춘 설계의 문제점
```java
// =========== 등록 ============
Team team = new Team();
team.setName("TeamA");
// team 영속화 & 저장
em.persist(team);

Member member = new Member();
member.setName("member1");
member.setTeamId(team.getId()); // team 객체의 참조가 아닌 teamId를 넣음
// member 영속화 & 저장
em.persist(member);


// =========== 조회 ============
Member findMember = em.find(Member.class, member.getId());
Long teamId = findMember.getTeamId();
Team findTeam = em.find(Team.class, teamId);
```

- 객체를 테이블에 맞춰 데이터 중심으로 모델링하면 객체 간 협력관계를 만들 수 없음
- FK를 사용해서 여러번 쿼리를 수행해야 함

#### 단방향 연관관계
- 맴버 → 팀
  ![Desktop View](/images/9.png)

#### 양방향 연관관계
- 맴버 → 팀
- 맴버 ← 팀
  ![Desktop View](/images/10.png)

- 테이블은 방향 개념이 없고 FK 하나로 양쪽 테이블 탐색이 가능
- 객체에서 양쪽 객체 모두를 탐색하려면 `필드(members)`가 필요함
- `@OneToMany(mappedBy = "team")` 애노테이션이 중요함

> `@OneToMany(mappedBy = "team")` <br>
    객체의 연관관계는 2개 <br>
     - 맴버 → 팀 1개의 단방향 <br>
     - 맴버 ← 팀 1개의 단방향 <br>
    테이블의 연관관계 1개 <br>
     - 맴버 ↔ 팀 1개의 양방향 <br> 

객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 함
```java
class A {
    B b;
}

class B {
    A a;
}

// A → B
a.getB();
// B → A
b.getA();
```

단방향 2개를 가지고 있는 객체 입장에서 팀을 바꾼다고 가정하면
 - 맴버 객체의 팀을 수정할 지
 - 팀 객체의 맴버를 수정할 지

선택하기 어려움.. → 양방향(단방향 2개) **연관관계의 주인**을 정해주자


**연관관계의 주인**
- 객체의 두 관계중 하나를 연관관계의 주인으로 지정
- 연관관계의 주인쪽에서 FK를 관리(등록, 수정)
- 연관관계의 주인이 아닌 쪽은 read-only / mappedBy 설정으로 주인을 Following
- DB의 FK가 존재하는 테이블이 주인

**연관관계 자주하는 실수**
```java
Member member = new Member();
member.setName("mingo");
em.persist(member);

Team team = new Team();
team.setName("플랫폼");
// team의 members 필드는 연관관계의 주인이 아니다!!!
team.getMembers().add(member);
em.persist(team);


em.flush();
em.clear();
```
![Desktop View](/images/11.png)

연관관계 주인이 아닌 team의 members 필드에게 데이터를 넣어도 실제 데이터 insert가 수행되지 않으므로 DB의 TEAM_ID는 null인 것을 확인할 수 있음

**연관관계의 값 설정**
- 연관관계 주인 / 연관관계 주인 팔로워 모두 값 설정을 해주자
  1. 연관관계 주인쪽만 값 설정을 해주어도 되지만 헷갈림 방지
  2. 연관관계 주인쪽만 해주었을 때, flush()가 발생하지 않은 동일한 트랙잭션 내 조회가 또 발생하였다고 가정하면 이슈가 발생함
     ```java
     Team team = new Team();
     team.setName("플랫폼");
     em.persist(team);

     Member member = new Member();
     member.setName("mingo");
     member.setTeam(team);
     em.persist(member);

     // em.flush();
     // em.clear();
     
     // 조회 발생 !!! 1차 캐시에서 데이터를 그대로 가져옴 -> 당연히 team에 members는 비어 있음
     Team findTeam = em.find(Team.class, team.getId());
     List<Member> members = findTeam.getMembers();
     members.forEach(s -> {
        System.out.println("맴버명단 :" + s);
     });   
     ```
  3. 객체지향적인 관점에서 봤을 때 양쪽 모두 값을 설정하는 것이 바람직함

