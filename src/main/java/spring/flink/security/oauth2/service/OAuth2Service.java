package spring.flink.security.oauth2.service;

import spring.flink.domain.enums.Social;
import spring.flink.web.dto.MemberResponseDTO;

public interface OAuth2Service {
    MemberResponseDTO.MemberLoginResultDTO oAuth2Login(String code);
    Social getProvider();
}
