package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
//MemberRepositoryCustom or MemberRepository + Impl

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
