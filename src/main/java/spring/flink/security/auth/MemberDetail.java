package spring.flink.security.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spring.flink.domain.Member;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Getter
public class MemberDetail implements UserDetails {
    // 사용자를 식별할 수 있는 정보(이름, 비밀번호, 권한)를 담은 객체
    private final Member member;

    public static MemberDetail createMemberDetail(Member member) {
        return new MemberDetail(member);
    }

    @Override
    // 사용자가 가진 권한(USER, ADMIN) 목록을 반환 -> 인가를 위함
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

