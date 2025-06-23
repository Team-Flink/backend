package spring.flink.security.oauth2.util.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import spring.flink.apiPayload.exception.GeneralException;
import spring.flink.apiPayload.status.ErrorStatus;
import spring.flink.security.oauth2.dto.provider.KakaoUserInfo;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoUtil {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUrl;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUrl;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 인가 코드(code)로 카카오 액세스 토큰(token) 발급 요청
    public KakaoUserInfo.OAuth2Token requestToken(String code) {

        // restTemplate에 넣을 HttpEntity의 header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // restTemplate에 넣을 HttpEntity의 body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("redirect_uri", redirectUrl);
        map.add("code", code);
//        map.add("client_secret", clientSecret);

        // restTemplate에 넣을 HttpEntity (POST라서 body도 필요)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // restTemplate로 카카오에 카카오 액세스 토큰 요청(POST)해서 String으로 받아오기
        ResponseEntity<String> response = restTemplate
                .exchange(tokenUrl, HttpMethod.POST, request, String.class);

        // try-catch로 String으로 받아온 결과에서 token 추출
        KakaoUserInfo.OAuth2Token token = null;
        try {
            token = objectMapper.readValue(response.getBody(), KakaoUserInfo.OAuth2Token.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
        return token;
    }

    // 카카오 액세스 토큰(token)으로 유저 정보(profile) 요청
    public KakaoUserInfo.KakaoProfile requestProfile(KakaoUserInfo.OAuth2Token token) {

        // restTemplate에 넣을 HttpEntity의 header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + token.getAccess_token());

        // restTemplate에 넣을 HttpEntity (GET이라 body 필요 없음)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // restTemplate로 카카오에 유저 정보 요청(GET)해서 String으로 받아오기
        ResponseEntity<String> response = restTemplate
                .exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        // try-catch로 String으로 받아온 결과에서 profile 추출
        KakaoUserInfo.KakaoProfile profile = null;
        try {
            profile = objectMapper.readValue(response.getBody(), KakaoUserInfo.KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.PARSE_ERROR);
        }
        return profile;
    }
}
