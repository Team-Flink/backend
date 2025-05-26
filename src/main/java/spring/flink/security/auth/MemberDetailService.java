package spring.flink.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.flink.domain.Member;
import spring.flink.repository.MemberRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 사용자 email로 사용자의 정보(Member)를 가져오는 메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByEmail((email));
        if(member.isEmpty()) throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다");
        return MemberDetail.createMemberDetail(member.get());
    }
}
