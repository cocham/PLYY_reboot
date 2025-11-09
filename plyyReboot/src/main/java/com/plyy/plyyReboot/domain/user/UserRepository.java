package com.plyy.plyyReboot.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // email을 기준으로 유저를 찾고 (계정 통합)
    // provider와 socialId로도 찾을 수 있습니다. (선택적)
    Optional<User> findByEmail(String email);

    // (선택) provider와 socialId로 찾는 로직 (이메일 동의 안했을 시 대비)
    Optional<User> findByProviderAndSocialId(String provider, String socialId);
}