package com.example.jpa.basic;

import com.example.jpa.shop.domain.level1.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ProxyMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setName("hello");

            em.persist(member);

            em.flush();
            em.clear();

            // getReference() 메서드를 호출하는 시점에는 SQL이 안나감
            Member referenceMember = em.getReference(Member.class, member.getId());

            // referenceMember.class = class com.example.jpa.shop.domain.level1.Member$HibernateProxy$n3hb6vEr
            System.out.println("referenceMember.class = " + referenceMember.getClass());
            // getId()는 DB를 안찾아가도 알 수 있는 정보
            System.out.println("referenceMember.id = " + referenceMember.getId());

            // !!! 실제 호출 !!! DB에 접근하지 않고서는 알 수 없는 데이터(name)를 사용할 때, SQL이 실행됨
            System.out.println("referenceMember.name = " + referenceMember.getName());

            Member m1 = new Member();
            m1.setName("m1");
            em.persist(m1);
            Member m2 = new Member();
            m2.setName("m2");
            em.persist(m2);

            em.flush();
            em.clear();

            Member mem1 = em.getReference(Member.class, m1.getId());
            Member mem2 = em.getReference(Member.class, m2.getId());

            System.out.println("mem1 ==  mem2 : " + (mem1.getClass() == mem2.getClass()));


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
