package spring.flink.security.oauth2.dto.provider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

public class KakaoUserInfo {

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
