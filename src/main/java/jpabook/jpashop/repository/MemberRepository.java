package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // JPA가 제공하는 표준 Annotation
    /*@PersistenceContext
    private EntityManager em;*/

    private final EntityManager em;


    // 회원을 저장하는 로직
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    // 회원의 아이디 값으로 회원을 찾는 로직
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    // 회원 전체 리스트 조회
    // sql과 jpql은 조금 다르다.. sql은 테이블을 대상으로 jpql은 엔티티들 대상으로..
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 이름을 통한 리스트 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
