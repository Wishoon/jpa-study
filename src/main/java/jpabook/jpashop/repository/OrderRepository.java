package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";

        return em.createQuery(jpql, Order.class)
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member " +
                        "join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrdersDto() {
        return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                                         " from Order o" +
                                         " join o.member m" +
                                         " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();

    }

    public List<Order> findAllWithItem() {
        return em.createQuery("select distinct o from Order o " +
                                        " join fetch o.member m" +
                                        " join fetch o.delivery d" +
                                        " join fetch o.orderItems oi" +
                                        " join fetch oi.item i", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member " +
                        "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

//    public List<Object> findAll1() {
//        String query = "select g.USER_ID, sum(p.PROGRAM_SCORE) from GIVE_SCORE g inner join g.PROGRAM p group by g.USER_ID";
//
//        List<Object> result = em.createQuery(query).getResultList();
//
//        return result;
//    }
//
//    public List<Object[]> findAll2() {
//        String query = "select g.USER_ID, sum(p.PROGRAM_SCORE) from GIVE_SCORE g inner join g.PROGRAM p group by g.USER_ID";
//
//        List<Object[]> result = em.createQuery(query).getResultList();
//
//        return result;
//    }
    /*public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m where o.status = :status and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)
                .getResultList();
    }*/
}
