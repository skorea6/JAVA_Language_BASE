package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
//            // Insert 할때
//            Member member = new Member();
//            member.setId(1L);
//            member.setName("HelloA");
//            em.persist(member);


//            // 찾을때
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember = " + findMember.getId());
//            System.out.println("findMember = " + findMember.getName());

//            // DELETE 할때
//            Member findMember = em.find(Member.class, 1L);
//            em.remove(findMember);

//            // UPDATE 할때 -> 중요! 그냥 이렇게만 치면 업데이트 된다고
//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("HelloJPA");





//            // JPQL로 조회
//            List<Member> result = em.createQuery("select m from Member as m", Member.class)
//                    .setFirstResult(5)
//                    .setMaxResults(8)
//                    .getResultList();
//
//            for (Member member : result) {
//                System.out.println("member.name = " + member.getName());
//            }





//            // 비영속 상태
//            Member member = new Member();
//            member.setId(101L);
//            member.setName("HelloJPA");
//
//            // 영속 상태
//            System.out.println("------- BEFORE -------");
//            em.persist(member); // sql 쿼리가 여기서 날라가는게 아니다.
//            System.out.println("------- AFTER -------");
//            // em.detach(member); // detach

//            // 위에 영속 상태 (persist)에서 한번 1차 캐싱을 했기 때문에, 아래에서는 SELECT 쿼리 없이 그냥 나온다.
//            Member findMember = em.find(Member.class, 101L);
//            System.out.println("findMember.id" + findMember.getId());
//            System.out.println("findMember.name" + findMember.getName());




//            // 영속 : findMember1 만 select 쿼리가 찍히고 findMember2는 select 쿼리가 나가지 않는다.
//            Member findMember1 = em.find(Member.class, 101L);
//            Member findMember2 = em.find(Member.class, 101L);
//            System.out.println("result = " + (findMember1 == findMember2)); // true -> 1차 캐시가 있기 때문에 가능한것.





//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");
//
//            em.persist(member1);
//            em.persist(member2);
//
//            System.out.println("==================="); // 아래 부분에서 쿼리가 나간다.


//            Member member = em.find(Member.class, 150L);
//            member.setName("ZZZZZ");
//            // em.persist(member); 넣지않아도 업데이트 된다! <진짜 중요!>






//            Member member = new Member(200L, "member200");
//            em.persist(member);
//
//            em.flush(); // commit 전에 flush 를 쓰면 바로 sql 쿼리가 나가버린다.
//
//            System.out.println("--------------------");





//            em.detach(member); // member만 준영속 상태로 만들기
//            em.clear(); // 통째로 모두 준영속 상태로 만들기 (1차 캐시를 통째로 지워버림)


//            // 아래는 객체 지향스럽지 않은! 코딩이다. team.getId 이렇게 셋팅하면 안된다~ setTeam(team) 이런식으로 뭔가를 만들어 제발
//            // 객체를 테이블에 맞추어 데이터 중심으로 모델링하면 협력 관계를 만들 수 없다.
//            Team team = new Team();
//            team.setName("TeamA");
//            em.persist(team);
//
//            Member member = new Member();
//            member.setName("member1");
//            member.setTeamId(team.getId());
//            em.persist(member);
//
//            Member findMember = em.find(Member.class, member.getId());
//            Long findTeamId = team.getId();
//            Team findTeam = em.find(Team.class, findTeamId);

            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setName("member1");
//            member.changeTeam(team);
            em.persist(member);

            team.addMember(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();

            for (Member m : members) {
                System.out.println("m = " + m.getName());
            }


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }

        emf.close();
    }
}
