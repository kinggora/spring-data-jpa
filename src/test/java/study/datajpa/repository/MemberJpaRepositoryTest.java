package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;

import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(saveMember.getUsername());

        assertThat(findMember).isEqualTo(saveMember);

    }

    @Test
    void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan(){
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("member", 15);
        assertThat(result.get(0)).isEqualTo(member2);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    void testNamedQuery(){
        Member memberA = new Member("AAA", 10);
        Member memberB = new Member("BBB", 20);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        assertThat(result.get(0)).isEqualTo(memberA);
        assertThat(result.size()).isEqualTo(1);
    }


    @Test
    void findByPageAndTotalCount(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 20);
        Member member4 = new Member("DDD", 30);
        Member member5 = new Member("FFF", 10);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5);

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(2);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    void bulkUpdate() throws Exception{
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 30);
        Member member4 = new Member("DDD", 40);
        Member member5 = new Member("FFF", 50);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5); //영속성 컨텍스트에 엔티티 저장

        int resultCount = memberJpaRepository.bulkAgePlus(25);

        assertThat(resultCount).isEqualTo(3);

    }
}