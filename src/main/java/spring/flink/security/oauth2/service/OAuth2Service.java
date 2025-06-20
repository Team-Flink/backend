package spring.flink.security.oauth2.service;

import spring.flink.domain.enums.Social;
import spring.flink.web.dto.MemberResponseDTO;

// 다양한 소셜 로그인 방식을 위한 인터페이스
public interface OAuth2Service {
    MemberResponseDTO.MemberLoginResultDTO oAuth2Login(String code);
    Social getProvider();
}
