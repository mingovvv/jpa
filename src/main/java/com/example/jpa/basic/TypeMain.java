package com.example.jpa.basic;

import com.example.jpa.shop.domain.level1.Member;
import com.example.jpa.shop.domain.level1.Team;
import com.example.jpa.shop.domain.level4.Child;
import com.example.jpa.shop.domain.level4.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TypeMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team1 = new Team();
            team1.setName("플랫폼");

            Team team2 = new Team();
            team2.setName("영업");

            Team team3 = new Team();
            team3.setName("프론트");

            Member member1 = new Member();
            member1.setName("a");
            member1.setTeam(team1);
            em.persist(member1);

            Member member2 = new Member();
            member2.setName("b");
            member2.setTeam(team1);
            em.persist(member2);

            Member member3 = new Member();
            member3.setName("c");
            member3.setTeam(team1);
            em.persist(member3);



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
