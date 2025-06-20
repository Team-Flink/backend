package spring.flink.security.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.flink.apiPayload.ApiResponse;
import spring.flink.domain.enums.Social;
import spring.flink.security.oauth2.service.OAuth2Service;
import spring.flink.security.oauth2.service.OAuth2ServiceFactory;
import spring.flink.security.oauth2.util.OAuth2ProviderResolver;
import spring.flink.web.dto.MemberResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuth2Controller {

    private final OAuth2ServiceFactory serviceFactory;

    @GetMapping("/{provider}")
    @Operation(summary = "Kakao 소셜 로그인 API",
            description = "인가 코드를 넣으면 액세스 토큰을 발급하고, 해당 토큰으로 유저 정보를 가져와 DB에 저장하고 서비스 토큰을 발급하는 API입니다.")
    @Parameters({
            @Parameter(name = "code", description = "인가 코드가 필요합니다."),
            @Parameter(name = "provider", description = "kakao를 입력해 주세요.")
    })
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberLoginResultDTO>> oAuth2Login(
            @RequestParam("code") String code,
            @PathVariable("provider") String provider) {

        // Social에 맞는 OAuth2Service 얻기
        Social social = OAuth2ProviderResolver.resolve(provider);
        OAuth2Service oAuth2Service = serviceFactory.getOAuth2Service(social);

        // 해당 OAuth2Service로 accessToken과 refreshToken 만들기
        MemberResponseDTO.MemberLoginResultDTO result = oAuth2Service.oAuth2Login(code);

        // 헤더에 담아 반환하기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + result.getAccessToken());
        headers.set("Refresh-Token", result.getRefreshToken());

        return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess(null));
    }
}
