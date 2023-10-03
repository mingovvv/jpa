package com.example.jpa.basic;

import com.example.jpa.basic.entity.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            // 등록
            Member member = new Member();
            member.setUsername("mingo");
            em.persist(member);

            System.out.println(member.getId());

            // 조회
            Member findById = em.find(Member.class, 1L);
            System.out.println(findById.getId()); // 1
            System.out.println(findById.getUsername()); // mingo

            // 수정
            findById.setUsername("mingovvv"); // dirty-checking

            // JPQL 사용 (엔티티 객체를 대상으로 쿼리하는 개념 -> 방언(Dialect가 바뀌어도 코드가 바뀌는 일은 없다.)
            List<Member> members = em.createQuery("select m from Member as m", Member.class).getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            // EntityManager가 DB session을 물고 있어서 꼭 닫아주어야 함
            em.close();
        }

        emf.close();

    }

}
