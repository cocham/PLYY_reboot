package com.plyy.plyyReboot.config.security.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 연결 및 템플릿 설정
 * Spring Boot의 RedisProperties를 활용하여 application.yml 설정 자동 바인딩
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    /**
     * Redis 연결 팩토리 생성
     * Jedis를 사용한 동기식 연결
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();

        factory.setHostName(redisProperties.getHost());
        factory.setPort(redisProperties.getPort());
        factory.setDatabase(redisProperties.getDatabase());

        if (redisProperties.getPassword() != null) {
            factory.setPassword(redisProperties.getPassword());
        }

        return factory;
    }

    /**
     * String 타입 전용 RedisTemplate 설정
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // String Serializer 사용 (토큰은 문자열이므로)
        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        // 트랜잭션 지원 비활성화 (단순 캐싱용)
        template.setEnableTransactionSupport(false);

        return template;
    }
}