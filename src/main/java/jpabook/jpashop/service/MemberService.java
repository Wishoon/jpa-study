package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // JPA의 모든 데이터 변경은 transaction 안에서 일어나야 한다.
@RequiredArgsConstructor
public class MemberService {

    /*@Autowired // 필드 인젝션
    MemberRepository memberRepository;*/

    /*private MemberRepository memberRepository;  // 세터 인젝션

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    // 생성자 인젝션
    private final MemberRepository memberRepository;

//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
    * 회원 가입
    * */
    @Transactional(readOnly = false)
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    // 동시에 가입을 요청했을때 validate를 통과하게 된다. 그래서 실무에서는 DB에 unique 제약조건을 통해 최후 방어를 하는 식으로 설계한다.
    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    @Transactional(readOnly = true) // 읽기에는 readOnly = true를 넣는것이 성능적으로 좋다!! <수정에는 넣으면 안됨!!!>
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
