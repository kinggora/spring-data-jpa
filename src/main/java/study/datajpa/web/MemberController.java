package study.datajpa.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.domain.Member;
import study.datajpa.domain.MemberDto;
import study.datajpa.domain.Team;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository temaRepository;

    @PostConstruct
    public void init(){
        Team team = new Team("spring-data-jpa");
        temaRepository.save(team);

        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("member"+i, i, team));
        }
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> pageDto = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));
        return pageDto;
    }

}








