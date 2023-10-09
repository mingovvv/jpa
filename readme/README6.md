# JPQL

-----

#### JPAL 이란?

 - JPA를 사용하면 엔티티 객체를 중심으로 개발
 - JPA는 SQL을 추상화한 JPQL을 지원함(ANSI 표준, 특정 DB에 의존하지 않음)
 - JPQL은 테이블이 아닌 엔티티 객체를 대상으로 쿼리를 수행하는 `객체지향 쿼리`
 - JPQL은 SQL로 변환되어 실행됨

```bigquery
// JPQL
select m from Member as m where m.age > 18
```
 - 엔티티(Member)와 속성(age)는 대소문자 구분
 - JPQL 키워드는 대소문자 구분X (select, from, where)
 - 테이블 명이 아닌 엔티티 명을 사용!
 - `별칭은 필수`(m, alias는 생략가능)

####
```java
// 반환 타입이 명확한 경우, TypedQuery
TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);

// 반환 타입이 명확하지 않은 경우, Query
Query query3 = em.createQuery("select m.username, m.age from Member m");
```

결과 조회
```java
// 반환 타입이 콜렉션일 경우, getResultList()
List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
// 반환 타입이 단 건일 경우, getSingleResult()
Member member = em.createQuery("select m from Member m where m.id = 1", Member.class).getSingleResult();
```
 - getResultList()
   - 결과가 하나 이상일 때
   - 결과가 없으면 빈 리스트 반환
 - getSingleResult()
   - 결과가 정확히 하나 일 때
   -  결과가 없으면 `NoResultException` 발생(spring data jpa를 사용하면 내부적으로 try-catch 코드가 있어 null 반환)
   -  결과가 둘 이상이면 `NonUniqueResultException` 발생

파라미터 바인딩
```java
// 이름기준
List<Member> members1 = em.createQuery("select m from Member m where m.username = :username", Member.class)
        .setParameter("username", "mingo")
        .getResultList();

// 위치기준
List<Member> members2 = em.createQuery("select m from Member m where m.username = ?1", Member.class)
        .setParameter(1, "mingo")
        .getResultList();
```

프로젝션
 - select절에 조회할 대상을 지정하는 것
 - 프로젝션 대상 : 엔티티 타입, 임베디드 타입, 스칼라 타입
 - 엔티티 프로젝션의 대상(반환 데이터)은 모두 `영속성 컨텍스트`에서 관리가 됨
   ```bigquery
   -- 엔티티 타입 프로젝션
   select m from Member m
   -- 엔티티 타입 프로젝션
   select m.team from Member m
   -- 임베디드 타입 프로젝션
   select m.address from Member m
   -- 스칼라 타입 프로젝션
   select m.username, m.age from Member m 
   ``` 
- 스칼라 타입이 여러개 일 때
  ```java
  // 방법 1
  List scalaList1 = em.createQuery("select m.username, m.age from Member m")
        .getResultList();
  Object o = scalaList1.get(0);
  Object[] result = (Object[]) o;
  String username1 = (String) result[0];
  int age1 = (int) result[1];

   // 방법 2
   List<Object[]> scalaList2 = em.createQuery("select m.username, m.age from Member m")
        .getResultList();
   Object[] result2 = scalaList2.get(0);
   String username2 = (String) result2[0];
   int age2 = (int) result2[1];

   // 방법 3 - DTO 생성, new 키워드를 통해 DTO 생성자 초기화
   List<Dto> scalaList3 = em.createQuery("select new com.example.jpa.Dto(m.username, m.age) from Member m", Dto.class)
        .getResultList();
   Dto result3 = scalaList3.get(0);
   String username3 = result3.username;
   int age3 = result3.age;
  ```
  
페이징
 - `setFirstResult(int startPosition)` : 조회 시작 인덱스
 - `setMaxResults(int limit)` : 조회할 데이터 수
 - `dialect(방언)` 설정에 따라 페이징 쿼리가 달리 나감(ex. mysql은 limit, oracle은 rownum...)
```java
em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0) // 시작 인덱스
                    .setMaxResults(5) // 갯수
                    .getResultList();
```

조인
 - JPA 2.1부터 `ON절` 사용 가능
 - 내부조인
   - `select m from Member m [inner] join m.team t`
 - 외부조인
   - `select m from Member m left [outer] join m.team t`
 - 세타조인
   - `select count(m) from Member m, Team t where m.username = t.name`

페치조인(fetch join)
 - 일반조인의 경우 from절 테이블만 영속화 시키는 반면, 페치조인의 경우 명시된 모든 테이블 영속화시킴 
 - jpal에서 성능 최적화를 위해 제공해주는 기능
 - 연관된 엔티티나 컬렉션을 sql 한 번에 함께 조회하는 기능
 - `join fetch` 명령어 사용
 - `N + 1` 문제 해결(즉시로딩(JPQL 사용 시) / 지연로딩 모두 `N + 1` 문제에서 자유로울 수 없다. fetch join 고려하기)
   ```bigquery
   -- SPQL
   select m from Member m join fetch m.team
   -- SQL
   select M.*, T.* from MEMBER M inner join TEAM t ON M.TEAM_ID = T.ID 
   ```
```java
String jpql = "select m from Member m join fetch m.team";
List<Member> members = em.createQuery(jpql, Member.class).getResultList();
for (Member member : members) {
   //페치 조인으로 회원과 팀을 함께 조회해서 지연 로딩X
   System.out.println("username = " + member.getUsername() + ", " + "teamName = " + member.getTeam().name());
}
```

서브쿼리
 - `from절`의 서브 쿼리는 하이버네이트6부터 지원함
```bigquery
-- 나이가 평균보다 많은 회원
select m from Member m where m.age > (select avg(m2.age) from Member m2)
-- 한 건이라도 주문한 고객
select m from Member m where (select count(o) from Order o where m = o.member) > 0
-- 팀A 소속인 회원
select m from Member m where exists (select t from m.team t where t.name = '팀A')
-- 전체 상품 각각의 재고보다 주문량이 많은 주문들
select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)
-- 어떤 팀이든 팀에 소속된 회원
select m from Member m where m.team = ANY (select t from Team t)
```

