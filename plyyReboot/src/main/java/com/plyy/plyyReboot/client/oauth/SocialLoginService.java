package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.client.oauth.attributes.OAuth2Attributes;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.domain.user.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final UserRepository userRepository;

    @Transactional
    public User loginOrRegister(OAuth2Attributes attrs) {
        String email = attrs.email().value();

        Optional<User> found = userRepository.findByEmail(email);

        if (found.isPresent()) {
            User user = found.get();
            user.updateLastLogin();
            return user;
        }

        User newUser = User.builder()
                .email(email)
                .nickname(null)
                .provider(attrs.provider().name())
                .socialId(attrs.providerId().value())
                .role(Role.NEW_USER.value())
                .build();

        return userRepository.save(newUser);
    }
}

