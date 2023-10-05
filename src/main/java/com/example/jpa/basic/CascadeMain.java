package com.example.jpa.basic;

import com.example.jpa.shop.domain.level1.Member;
import com.example.jpa.shop.domain.level1.Team;
import com.example.jpa.shop.domain.level4.Child;
import com.example.jpa.shop.domain.level4.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class CascadeMain {

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Child child1 = new Child();
            child1.setName("첫째");

            Child child2 = new Child();
            child2.setName("둘째");

            Parent parent = new Parent();
            parent.setName("부모님");
            child1.addParent(parent);
            child2.addParent(parent);
//            parent.addChild(child1);
//            parent.addChild(child2);

            /**
             * 연관관계를 모두 영속화 시켜야하는 귀찮음이 발생...
             */
//            em.persist(child1);
//            em.persist(child2);
//            em.persist(parent);

            /**
             * @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
             * 설정을 통해 한번에 자식들까지 영속화
             */
            em.persist(parent);

            em.flush();
            em.clear();

            System.out.println("=====");

            Parent findParent = em.find(Parent.class, parent.getId());

            /**
             * @OneToMany(mappedBy = "parent", orphanRemoval = true)
             * 끊어진 자식들은 삭제쿼리 수행
             *
             *   => delete from Child where id=?
             */
            findParent.getChildList().remove(0);



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
