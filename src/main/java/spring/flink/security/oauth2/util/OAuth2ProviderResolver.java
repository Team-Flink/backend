package spring.flink.security.oauth2.util;

import spring.flink.apiPayload.exception.GeneralException;
import spring.flink.apiPayload.status.ErrorStatus;
import spring.flink.domain.enums.Social;

// String으로 받아온 provider를 Social로 반환
public class OAuth2ProviderResolver {

    public static Social resolve(String provider) {
        try {
            return Social.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_SOCIAL);
        }
    }
}
