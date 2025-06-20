package spring.flink.security.oauth2.service;

import org.springframework.stereotype.Component;
import spring.flink.domain.enums.Social;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Social과 OAuth2Service 구현체를 맵으로 저장
// Social에 맞는 OAuth2Service 구현체를 꺼내는 용도
@Component
public class OAuth2ServiceFactory {

    private final Map<Social, OAuth2Service> serviceMap;

    public OAuth2ServiceFactory(List<OAuth2Service> serviceList) {
        this.serviceMap = serviceList.stream()
                .collect(Collectors.toMap(OAuth2Service::getProvider, Function.identity()));
    }

    public OAuth2Service getOAuth2Service(Social social) {
        return serviceMap.get(social);
    }
}
