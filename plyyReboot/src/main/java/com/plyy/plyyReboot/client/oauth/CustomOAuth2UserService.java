package com.plyy.plyyReboot.client.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. 유저 정보(Map)를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. registrationId (kakao, naver, google)를 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. DTO를 사용해 공급자별로 유저 정보를 파싱
        OAuth2UserInfo userInfo = createUserInfo(registrationId, attributes);

        // 3.1 파싱한 이메일을 attributes 맵에 추가
        // (SuccessHandler가 이메일을 바로 찾을 수 있도록)
        String email = userInfo.getEmail();
        attributes.put("plyy_provider_email", email);

        // 4. 이메일로 DB에서 유저 찾기 (계정 통합)
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

        /// 5. Spring Security의 Principal 객체를 반환 (수정)
        // DB에서 가져온 user의 실제 Role("ROLE_NEW_USER" 등)을
        // Spring Security가 인식할 수 있는 권한 목록(GrantedAuthority)으로 변환
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        // 5.1 리팩토링 2단계: "name"으로 사용할 키를 "plyy_provider_email"로 지정함
        return new DefaultOAuth2User(
                authorities, // null 대신 authorities 변수를 전달
                attributes,
                "plyy_provider_email" // Pricipal의 "name"이 이 키의 값(이메일)이 됨
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

    private User registerNewUser(OAuth2UserInfo userInfo, String registrationId) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(null)
                .provider(registrationId)
                .socialId(userInfo.getProviderId())
                .role("ROLE_NEW_USER")
                .build();

        return userRepository.save(user);
    }
}