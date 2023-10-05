package com.example.jpa.basic;

import com.example.jpa.shop.domain.level1.Member;
import com.example.jpa.shop.domain.level1.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class LazyAndEagerMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("플랫폼");


            Member member = new Member();
            member.setName("hello");
            member.setTeam(team);

            em.persist(team);
            em.persist(member);

            em.flush();
            em.clear();

            /**
             * @ManyToOne(fetch = FetchType.LAZY)
             * select
             *         member0_.id as id1_0_0_,
             *         member0_.USERNAME as username2_0_0_,
             *         member0_.TEAM_ID as team_id3_0_0_
             *     from
             *         Member member0_
             *     where
             *         member0_.id=?
             *
             * @ManyToOne(fetch = FetchType.EAGER)
             * select
             *         member0_.id as id1_0_0_,
             *         member0_.USERNAME as username2_0_0_,
             *         member0_.TEAM_ID as team_id3_0_0_,
             *         team1_.id as id1_1_1_,
             *         team1_.name as name2_1_1_
             *     from
             *         Member member0_
             *     left outer join
             *         Team team1_
             *             on member0_.TEAM_ID=team1_.id
             *     where
             *         member0_.id=?
             *
             */
            Member findMember = em.find(Member.class, member.getId());

            /**
             * LAZY 설정 일 때, 프록시 : class com.example.jpa.shop.domain.level1.Team$HibernateProxy$3O3qdjUn
             * EAGER 설정 일 때, 객체 : class com.example.jpa.shop.domain.level1.Team
             */
            System.out.println(findMember.getTeam().getClass());

            em.flush();
            em.clear();

            /**
             * Member의 team 필드는 @ManyToOne(fetch = FetchType.EAGER) 로 설정한 뒤
             * 아래의 JPQL을 수행하면,
             * member / team 조회 쿼리가 각각 수행됨
             *
             * why?
             *  - find()메서드를 통한 접근은 jpa 최적화를 지원하기 때문에 내부적으로 join을 수행함
             *  - 하지만 JPQL은 우선 명시된 쿼리를 바탕으로 동작해서 member 조회 쿼리가 먼저 나간 것
             *  - 그 뒤 필드에 team이 있는것을 보고 내부적으로 한번 더 team 조회 쿼리를 수행
             *
             * N + 1 문제 발생
             *   - 만약 member의 조회 결과로 100개의 리스트가 반환되었다면?
             *   - 100개 각각 team을 조회하는 쿼리가 나감 -> 총 101번의 쿼리가 수행되는 것
             */
            List<Member> members = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

//            List<Member> members2 = em.createQuery("select m from Member m join fetch m.team", Member.class)
//                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // EntityManager가 DB session을 물고 있어서 꼭 닫아주어야 함
            em.close();
        }

        emf.close();

    }

}
