package spring.flink.security.oauth2.service.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.flink.domain.Member;
import spring.flink.domain.enums.Social;
import spring.flink.repository.MemberRepository;
import spring.flink.security.jwt.JwtTokenProvider;
import spring.flink.security.oauth2.converter.OAuth2Converter;
import spring.flink.security.oauth2.dto.provider.KakaoUserInfo;
import spring.flink.security.oauth2.service.OAuth2Service;
import spring.flink.security.oauth2.util.provider.KakaoUtil;
import spring.flink.web.dto.MemberResponseDTO;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuth2Service implements OAuth2Service {

    private final KakaoUtil kakaoUtil;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public MemberResponseDTO.MemberLoginResultDTO oAuth2Login(String code) {

        // kakaoUtil에 인가 코드를 넘겨 카카오 액세스 토큰 요청
        KakaoUserInfo.OAuth2Token token = kakaoUtil.requestToken(code);

        // kakaoUtil에 액세스 토큰을 넘겨 유저 정보 요청
        KakaoUserInfo.KakaoProfile profile = kakaoUtil.requestProfile(token);
        String email = profile.getKakao_account().getEmail();
        String nickname = profile.getKakao_account().getProfile().getNickname();

        // 이미 있는 유저라면 꺼내고, 아니라면 DB에 새로 저장
        // Social이 LOCAL이 아닌(소셜 로그인) 사용자는 password가 필요 없지만 일단 임시로 저장함
        Member member = memberRepository.findByEmail(email).orElseGet(() -> {
            Member newMember = OAuth2Converter
                    .toMember(Social.KAKAO, email, nickname,
                            passwordEncoder.encode(LocalDateTime.now().toString()));
            return memberRepository.save(newMember);
        });

        // 액세스 토큰과 리프레시 토큰 발급
        // 리프레시 토큰은 Redis에 따로 저장
        String accessToken = jwtTokenProvider.makeToken(member.getId(),email,1);
        String refreshToken = jwtTokenProvider.makeToken(member.getId(),email,2);
        long refreshTokenEx = jwtTokenProvider.getExpiration(refreshToken);
        redisTemplate.opsForValue().set("refresh"+email, refreshToken, refreshTokenEx, TimeUnit.MILLISECONDS);

        // 액세스 토큰으로 Authentication을 만들어 SecurityContextHolder에 저장
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new MemberResponseDTO.MemberLoginResultDTO(accessToken,refreshToken);
    }

    @Override
    public Social getProvider() {
        return Social.KAKAO;
    }
}
