package spring.flink.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class JwtProperties {

    @Value("${spring.jwt.secret}")
    private String secret;

    // 시크릿을 기반으로 인코딩?
    //private SecretKey secretKey;

    @Value("${spring.jwt.token.access_expiration}")
    private Long accessExpireMS;

    @Value("${spring.jwt.token.refresh_expiration}")
    private Long refreshExpireMS;


}
