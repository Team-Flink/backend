package spring.flink.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import spring.flink.apiPayload.exception.GeneralException;
import spring.flink.apiPayload.status.ErrorStatus;
import spring.flink.security.auth.MemberDetail;
import spring.flink.security.auth.MemberDetailService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider implements InitializingBean {

    private final MemberDetailService memberDetailService;

    @Value("${spring.jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @Value("${spring.jwt.token.access_expiration}")
    private Long accessExpireMS;

    @Value("${spring.jwt.token.refresh_expiration}")
    private Long refreshExpireMS;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //  토큰을 생성
    public String makeToken(Long memberId, String email, int type){
        Date now = new Date();
        Long ex = (type == 1) ? accessExpireMS : refreshExpireMS;
        String tokenType = (type == 1) ? "accessToken" : "refreshToken";
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("tokenType", tokenType)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ex))
                .signWith(secretKey)
                .compact();
    }

    // 토큰을 추출하기
    public String resolveToken(HttpServletRequest req){
        String header = req.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            return null;
        }
        return header.substring(7).trim();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new GeneralException(ErrorStatus.WRONG_TYPE_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new GeneralException(ErrorStatus.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new GeneralException(ErrorStatus.WRONG_TYPE_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new GeneralException(ErrorStatus.NOT_VALID_TOKEN);
        }
    }

    public Authentication getAuthentication(String token){
        MemberDetail memberDetail = memberDetailService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(memberDetail, null, memberDetail.getAuthorities());
    }

    public String getEmail(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public long getExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();
    }

}
