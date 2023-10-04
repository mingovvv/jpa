package com.example.jpa.basic;

import com.example.jpa.shop.domain.Member;
import com.example.jpa.shop.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ShopMain {

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
            member.setTeam(team);
            member.setName("mingo");

            // 1차 캐시 등록
            em.persist(team);
            // 1차 캐시 등록
            em.persist(member);

            em.flush();
            // 준영속화 -> 1차 캐시의 관리대상에서 벗어남
            em.clear();

            /**
             * 현재 기본 키 전략이 @GeneratedValue 로 설정되어 있음
             * 세부옵션을 선택하지 않은 경우, strategy = GenerationType.AUTO 로 적용되며
             * 테스트 중인 H2 디비의 경우 시퀀스 전략을 선택한다
             * 스퀀스 전략의 경우 commit이 되기전까지 실제 DB에 SQL이 전달되지 않으니
             * clear()로 준영속화로 만들어봤자 조회되는 데이터는 없을 것이다
             * 꼭 flush()로 영속화된 1차캐시 데이터의 SQL을 DB로 전달시켜주자
             *
             * [참고]
             * strategy = GenerationType.IDENTITY 전략의 경우 persist() 영속화 하자마자 SQL이 날아가니
             * 굳이 flush() 할 필요는 없음
             */

            // 1차 캐시에서 해당 데이터가 없기 때문에 DB와 통신해서 데이터를 조회해옴
            Member findMember = em.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam();

            System.out.println("findMember : " + findMember);
            System.out.println("findTeam : " + findTeam);



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
