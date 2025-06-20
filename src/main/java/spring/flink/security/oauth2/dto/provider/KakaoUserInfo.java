package spring.flink.security.oauth2.dto.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

// 카카오 소셜 로그인 유저의 카카오 액세스 토큰과 정보를 담는 DTO
// @JsonIgnoreProperties로 DTO에 맞지 않는 결과는 무시
public class KakaoUserInfo {

    // 카카오 액세스 토큰
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OAuth2Token {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private String scope;
        private int expires_in;
        private int refresh_token_expires_in;
    }

    // 유저 정보
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoProfile {
        private Long id;
        private String connected_at;
        private Properties properties;
        private KakaoAccount kakao_account;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Properties {
            private String nickname;
        }

        // 유저 정보 중 대부분 여기서 값을 꺼냄
        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class KakaoAccount {
            private String email;
            private Boolean has_email;
            private Boolean is_email_verified;
            private Boolean profile_nickname_needs_agreement;
            private Boolean email_needs_agreement;
            private Boolean is_email_valid;
            private Profile profile;

            @Getter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Profile {
                private String nickname;
                private Boolean is_default_nickname;
            }
        }
    }
}
