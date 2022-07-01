package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.MemberDto;
import study.datajpa.domain.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    void testMember(){
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(saveMember.getUsername());
        assertThat(findMember).isEqualTo(saveMember);

    }

    @Test
    void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }


    @Test
    void findByUsernameAndAgeGreaterThan(){
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("member", 15);
        assertThat(result.get(0)).isEqualTo(member2);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testNamedQuery(){
        Member memberA = new Member("AAA", 10);
        Member memberB = new Member("BBB", 20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> result = memberRepository.findByUsername("AAA");
        assertThat(result.get(0)).isEqualTo(memberA);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testQuery(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("AAA", 20);
        Member member4 = new Member("BBB", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> result = memberRepository.findUser("AAA", 20);
        assertThat(result.get(0)).isEqualTo(member3);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void findUsernameList(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 20);
        Member member4 = new Member("DDD", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<String> result = memberRepository.findUsernameList();
        for (String name : result) {
            System.out.println("member.username = " + name);
        }
        assertThat(result.size()).isEqualTo(4);

    }

    @Test
    void findMemberDto(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("AAA", 10, teamA);
        Member member2 = new Member("BBB", 20, teamB);
        Member member3 = new Member("CCC", 20, teamB);
        Member member4 = new Member("DDD", 30, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    void findByNames(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 20);
        Member member4 = new Member("DDD", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB", "CCC"));
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void findPageAndSliceByAge(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 10);
        Member member3 = new Member("CCC", 10);
        Member member4 = new Member("DDD", 10);
        Member member5 = new Member("FFF", 10);
        Member member6 = new Member("GGG", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);

        int age = 10;
        int index = 0;
        int size = 3;

        //페이지 인덱스(0부터), 페이지 당 데이터 개수(, 정렬 방식)
        PageRequest pageRequest = PageRequest.of(index, size, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findPageByAge(age, pageRequest); //FFF, DDD, CCC, BBB, AAA
        List<Member> content = page.getContent(); //조회된 데이터

        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername()));

        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?

        /* 다음 페이지(슬라이스) */
        Slice<Member> nextSlice = memberRepository.findSliceByAge(age, page.nextPageable()); //limit+1 조회
        List<Member> nextContent = nextSlice.getContent();
        for (Member member : nextContent) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
        assertThat(nextSlice.isLast()).isTrue(); //마지막 페이지인가?
        assertThat(nextSlice.hasNext()).isFalse(); //다음 페이지가 있는가?
    }

    @Test
    void bulkUpdate() throws Exception {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        Member member3 = new Member("CCC", 30);
        Member member4 = new Member("DDD", 40);
        Member member5 = new Member("FFF", 50);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5); //영속성 컨텍스트에 엔티티 저장

        int resultCount = memberRepository.bulkAgePlus(25);

        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    void findMemberLazy() throws Exception {
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        Team teamB = new Team("teamB");
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            member.getTeam().getName(); //Lazy 강제 초기화
        }

    }

    @Test
    void findMemberFetchJoin(){
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        Team teamB = new Team("teamB");
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberFetchJoin();

        em.flush();
        em.clear();

        List<Member> memberByAge = memberRepository.findMemberByAge(10);
        assertThat(memberByAge.get(0).getUsername()).isEqualTo("member1");
    }
}