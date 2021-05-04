package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);


//            // 기본 문법
//            Member result = em
//                    .createQuery("select m from Member m where m.usermame = :username", Member.class)
//                    .setParameter("username", "member1")
//                    .getSingleResult();
//            System.out.println("singleResult = " + result.getUsername());


            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();
            System.out.println("result.size = " + result.size());
            for (Member member1 : result) {
                System.out.println("member1 = " + member1.getUsername());
            }

//            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);

//            // 결과 값이 여러개 일때
//            List<Member> resultList = query1.getResultList();
//            for (Member member1 : resultList) {
//                System.out.println(member1.getAge());
//            }

//            // 결과 값이 한개(싱글) 일때 -> 결과가 2개이상이거나 없을때 Exception 터짐.
//            Member singleResult = query1.getSingleResult();
//            System.out.println("result = " + singleResult.getAge());


            // 반환 타입이 명확할때 (TypeQuery)
            //TypedQuery<String> query2 = em.createQuery("select m.usermame from Member m", String.class);

            // 반환 타입이 명확하지 않을때 (query, username은 String 인테 age 는 int)
            //Query query3 = em.createQuery("select m.usermame, m.age from Member m");





            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
