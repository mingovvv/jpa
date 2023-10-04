package com.example.jpa.basic;

import com.example.jpa.shop.domain.level3.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class InheritanceMain {

    /**
     * [조인 전략]
     * > 테이블 생성
     * create table Album (
     *     artist varchar(255),
     *     id bigint not null,
     *     primary key (id)
     * )
     *
     * create table Item (
     *     id bigint not null,
     *     name varchar(255),
     *     price integer not null,
     *     primary key (id)
     * )
     *
     * [싱글 테이블 전략]
     *
     * > 테이블 생성
     * create table Item (
     *     DTYPE varchar(31) not null,
     *     id bigint not null,
     *     name varchar(255),
     *     price integer not null,
     *     artist varchar(255),
     *     actor varchar(255),
     *     director varchar(255),
     *     author varchar(255),
     *     isbn varchar(255),
     *     primary key (id)
     * )
     *
     * []
     *
     * > 테이블 생성
     * create table Album (
     *        id bigint not null,
     *         name varchar(255),
     *         price integer not null,
     *         artist varchar(255),
     *         primary key (id)
     *     )
     * create table Book (
     *        id bigint not null,
     *         name varchar(255),
     *         price integer not null,
     *         author varchar(255),
     *         isbn varchar(255),
     *         primary key (id)
     *     )
     *  create table Movie (
     *        id bigint not null,
     *         name varchar(255),
     *         price integer not null,
     *         actor varchar(255),
     *         director varchar(255),
     *         primary key (id)
     *     )
     */

    public static void main(String[] args) {

        // EntityManagerFactory는 애플리케이션 로딩시점에 DB당 하나 생성됨
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 쓰레드에 종속적으로 설계해야 함 -> 쓰레드 간 절대 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Movie movie = new Movie();
            movie.setDirector("mingo");
            movie.setActor("mingo");
            movie.setName("mingo's holiday");
            movie.setPrice(10000);

            /**
             * [조인 전략 / 등록]
             *
             * insert into
             * Item (name, price, id)
             * values (?, ?, ?)
             *
             * insert into
             * Movie (actor, director, id)
             * values (?, ?, ?)
             *
             * [단일 테이블 전략 / 등록]
             *
             * insert  into
             * Item (name, price, actor, director, DTYPE, id)
             * values (?, ?, ?, ?, 'Movie', ?)
             *
             * [구현 클래스마다 테이블 생성 전략 / 등록]
             *
             * insert into
             * Movie (name, price, actor, director, id)
             * values (?, ?, ?, ?, ?)
             */
            em.persist(movie);

            em.flush();
            em.clear();

            /**
             * [조인 전략 / 조회]
             * select
             *         movie0_.id as id1_2_0_,
             *         movie0_1_.name as name2_2_0_,
             *         movie0_1_.price as price3_2_0_,
             *         movie0_.actor as actor1_3_0_,
             *         movie0_.director as director2_3_0_
             *     from
             *         Movie movie0_
             *     inner join
             *         Item movie0_1_
             *             on movie0_.id=movie0_1_.id
             *     where
             *         movie0_.id=?
             *
             * [단일 테이블 전략 / 조회]
             * select
             *         movie0_.id as id2_0_0_,
             *         movie0_.name as name3_0_0_,
             *         movie0_.price as price4_0_0_,
             *         movie0_.actor as actor6_0_0_,
             *         movie0_.director as director7_0_0_
             *     from
             *         Item movie0_
             *     where
             *         movie0_.id=?
             *         and movie0_.DTYPE='Movie'
             *
             * [구현 클래스마다 테이블 생성 전략 / 조회]
             * select
             *         movie0_.id as id1_2_0_,
             *         movie0_.name as name2_2_0_,
             *         movie0_.price as price3_2_0_,
             *         movie0_.actor as actor1_3_0_,
             *         movie0_.director as director2_3_0_
             *     from
             *         Movie movie0_
             *     where
             *         movie0_.id=?
             *
             */
            Movie findMovie = em.find(Movie.class, movie.getId());
            System.out.println("findMovie = " + findMovie);

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
