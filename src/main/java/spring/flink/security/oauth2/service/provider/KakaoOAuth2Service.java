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
        KakaoUserInfo.OAuth2Token token = kakaoUtil.requestToken(code);
        KakaoUserInfo.KakaoProfile profile = kakaoUtil.requestProfile(token);
        String email = profile.getKakao_account().getEmail();
        String nickname = profile.getKakao_account().getProfile().getNickname();

        Member member = memberRepository.findByEmail(email).orElseGet(() -> {
            Member newMember = OAuth2Converter
                    .toMember(Social.KAKAO, email, nickname,
                            passwordEncoder.encode(LocalDateTime.now().toString()));
            return memberRepository.save(newMember);
        });

        String accessToken = jwtTokenProvider.makeToken(member.getId(),email,1);
        String refreshToken = jwtTokenProvider.makeToken(member.getId(),email,2);
        long refreshTokenEx = jwtTokenProvider.getExpiration(refreshToken);
        redisTemplate.opsForValue().set("refresh"+email, refreshToken, refreshTokenEx, TimeUnit.MILLISECONDS);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new MemberResponseDTO.MemberLoginResultDTO(accessToken,refreshToken);
    }

    @Override
    public Social getProvider() {
        return Social.KAKAO;
    }
}
