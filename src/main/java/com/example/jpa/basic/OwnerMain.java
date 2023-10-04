package com.example.jpa.basic;

import com.example.jpa.shop.domain.level1.Member;
import com.example.jpa.shop.domain.level1.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class OwnerMain {

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
            // team의 members 필드는 연관관계의 주인이 아니다!!!
//            team.getMembers().add(member);
            em.persist(team);

            Member member = new Member();
            member.setName("mingo");
            member.setTeam(team);
            em.persist(member);

            // flush()
//            em.flush();
//            em.clear();

            Team findTeam = em.find(Team.class, team.getId());
            List<Member> members = findTeam.getMembers();
            members.forEach(s -> {
                System.out.println("맴버명단 :" + s);
            });


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
