package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.client.oauth.dto.GoogleOAuth2UserInfo;
import com.plyy.plyyReboot.client.oauth.dto.KakaoOAuth2UserInfo;
import com.plyy.plyyReboot.client.oauth.dto.NaverOAuth2UserInfo;
import com.plyy.plyyReboot.client.oauth.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. 유저 정보(Map)를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. registrationId (kakao, naver, google)를 확인합니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. DTO를 사용해 공급자별로 유저 정보를 파싱합니다.
        OAuth2UserInfo userInfo = createUserInfo(registrationId, attributes);

        // 4. 이메일로 DB에서 유저를 찾습니다. (계정 통합)
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());

        User user;
        if (userOptional.isPresent()) {
            // (A) 이미 가입한 유저 (로그인)
            user = userOptional.get();
            user.updateLastLogin(); // 마지막 로그인 시간 갱신
        } else {
            // (B) 신규 유저 (회원가입)
            user = registerNewUser(userInfo, registrationId);
        }

        // 5. Spring Security의 Principal 객체를 반환합니다.
        // (주의: DefaultOAuth2User를 반환해야 SuccessHandler가 Map을 읽을 수 있습니다)
        return new DefaultOAuth2User(
                null, // (권한은 SecurityConfig에서 처리)
                attributes,
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    // (Helper) 공급자별로 DTO를 생성하는 팩토리 메소드
    private OAuth2UserInfo createUserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("kakao")) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("naver")) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
    }

    // (Helper) 신규 유저를 DB에 저장하는 메소드
    private User registerNewUser(OAuth2UserInfo userInfo, String registrationId) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname()) // (닉네임 중복 체크 로직 추가 필요)
                .thumbnailUrl(userInfo.getProfileImageUrl())
                .provider(registrationId)
                .socialId(userInfo.getProviderId())
                .role("ROLE_USER") // (기본 역할)
                .build();

        return userRepository.save(user);
    }
}