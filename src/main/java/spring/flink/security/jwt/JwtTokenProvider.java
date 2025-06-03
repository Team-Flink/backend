package spring.flink.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class JwtTokenProvider{

    private final JwtProperties jwtProperties;
    private final MemberDetailService memberDetailService;

    //  토큰을 생성
    public String makeToken(Long memberId, String email, int type){
        Date now = new Date();
        Long ex = (type == 1) ? jwtProperties.getAccessExpireMS() :  jwtProperties.getRefreshExpireMS();
        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("tokenType", "accessToken")
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ex))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), SignatureAlgorithm.HS256)
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
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch(Exception e){
            return false;
        }

    }

    public Authentication getAuthentication(String token){
        MemberDetail memberDetail = memberDetailService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(memberDetail, null, memberDetail.getAuthorities());
    }

    public String getEmail(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public boolean isExpired(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

}
