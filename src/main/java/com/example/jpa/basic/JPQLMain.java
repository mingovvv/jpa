package com.example.jpa.basic;

import com.example.jpa.shop.domain.level1.Member;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JPQLMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            /**
             * JPAL
             */
            List<Member> resultList1 = em.createQuery("select m from Member m where m.name like '%ig%'", Member.class)
                    .getResultList();

            /**
             * Criteria
             */
            //Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);
            //루트 클래스 (조회를 시작할 클래스)
            Root<Member> m = query.from(Member.class);
            //쿼리 생성
            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("name"), "ig"));
            List<Member> resultList2 = em.createQuery(cq).getResultList();

            /**
             * native SQL
             */
            String sql = "SELECT * FROM MEMBER WHERE USERNAME like '%ig%'";
            List<Member> resultList3 = em.createNativeQuery(sql, Member.class).getResultList();

            /**
             * JPAL 시작
             */
            em.flush();
            em.clear();

            Member member = new Member();
            member.setName("mingo");
            em.persist(member);

            /**
             * 반환타입
             */
            // 반환 타입이 명확한 경우, TypedQuery
            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
//            List<Member> members = query1.getResultList();
//            Member member = query1.getSingleResult();

            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);

            // 반환 타입이 명확하지 않은 경우, Query
            Query query3 = em.createQuery("select m.username, m.age from Member m");

            /**
             * 파라미터 바인딩
             */
            // 이름기준
            List<Member> members1 = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "mingo")
                    .getResultList();

            // 위치기준
            List<Member> members2 = em.createQuery("select m from Member m where m.username = ?1", Member.class)
                    .setParameter(1, "mingo")
                    .getResultList();

            /**
             * 프로젝션
             */
            List<Member> members3 = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            // 프로젝션 스칼라 타입 - 여러값 조회
            List scalaList1 = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();
            Object o = scalaList1.get(0);
            Object[] result = (Object[]) o;
            String username1 = (String) result[0];
            int age1 = (int) result[1];

            List<Object[]> scalaList2 = em.createQuery("select m.username, m.age from Member m")
                    .getResultList();
            Object[] result2 = scalaList2.get(0);
            String username2 = (String) result2[0];
            int age2 = (int) result2[1];

            List<Dto> scalaList3 = em.createQuery("select new com.example.jpa.Dto(m.username, m.age) from Member m", Dto.class)
                    .getResultList();
            Dto result3 = scalaList3.get(0);
            String username3 = result3.username;
            int age3 = result3.age;

            /**
             * 페이징
             */
            em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0) // 시작 인덱스
                    .setMaxResults(5) // 갯수
                    .getResultList();


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

    static class Dto {
        String username;
        int age;

    }

}
