package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

    public List<Order> findAllByString(OrderSearch orderSearch) {
//        em.createQuery("select o from Order o join o.member m" +
//                " where o.status = :status" +
//                " and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                // .setFirstPosition(100) -> 페이징 할때 유용 100부터 1000개 가져오는..
//                .setMaxResults(1000)
//                .getResultList();
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }


    /**
     * JPA Criteria
     *
     * @param //orderSearch
     * @return
     */
//    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
//
//    }

    public List<Order> findAllWithMemberRepository() {
        return em.createQuery(
                "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
        .getResultList();
    }


    // 약간 비추
    // fetch 조인 중요! 한방 쿼리리
   public List<Order> findAllWithItem() {
        // distinct(중복제거 역할) 가 없을 경우 44 11 이렇게 두개씩 뜬다.. (1대다 관계이기 때문!!)
        // 실제 db 쿼리와는 다를 수 있다.

       //패치조인(일대다 관계에서)은 페이징 불가!! 메모리를 이용하기 떄문에 위험하다...
        return em.createQuery("select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems io" + // orders->order_item 이 일대다 관계
                " join fetch io.item i", Order.class).getResultList();
    }


    // 추천
    // 아래는 일대다 관계(orderItems)에서 fetch join 을 적용하지 않음으로서, 페이징이 가능해짐
    // 일대다 관계가 없기 때문에 중복도 발생하지 않아, distinct 를 넣어야할 이유도 없어지는것.
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
