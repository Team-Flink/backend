package spring.flink.security.oauth2.converter;

import spring.flink.domain.Member;
import spring.flink.domain.enums.Social;

// 삭제하고 기존 MemberConverter 수정해서 써도 됨
public class OAuth2Converter {

    public static Member toMember(Social social, String email, String nickname, String password) {
        return Member.builder()
                .social(social)
                .email(email)
                .nickname(nickname)
                .password(password)
                .phoneNumber("010-0000-0000") // 권한 없어서 못 가져옴 -> 임시로 저장
                .build();
    }
}
