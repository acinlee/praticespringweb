package jpapratice.jpc.service;

import jpapratice.jpc.domain.Member;
import jpapratice.jpc.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        Member member = new Member();
        member.setName("kim");

        Long saveId = memberService.join(member);

        Assertions.assertThat(member).isEqualTo(memberRepository.findOne(saveId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        Member member1  = new Member();
        member1.setName("kim1");

        Member member2  = new Member();
        member2.setName("kim1");

        memberService.join(member1);
        try{
            memberService.join(member2);
        }catch (IllegalStateException e) {
            return;
        }

        Assertions.fail("예외가 발생해야 한다.");
    }
}