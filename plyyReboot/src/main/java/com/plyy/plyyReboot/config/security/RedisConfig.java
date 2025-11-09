package com.plyy.plyyReboot.config.security; // (님의 패키지 경로)

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
// (LettuceConnectionFactory import는 이제 필요 없으니 삭제)

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // ▼▼▼▼▼ [ 1. 이 줄을 추가해서 .yml의 database 값을 읽어옵니다 ] ▼▼▼▼▼
    @Value("${spring.data.redis.database}")
    private int database;
    // ▲▲▲▲▲ [ "database: 0" 설정이 여기로 들어옵니다 ] ▲▲▲▲▲

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);

        // ▼▼▼▼▼ [ 2. 이 줄을 추가해서 0번 DB를 강제로 설정합니다 ] ▼▼▼▼▼
        factory.setDatabase(database);
        // ▲▲▲▲▲ [ "factory.setDatabase(0)"이 실행됩니다 ] ▲▲▲▲▲

        return factory; // 100% 동기 팩토리
    }

    // ★★★ 이 빈(Bean)이 모든 문제를 해결합니다 ★★★
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // 이제 이 팩토리는 host, port, "database 0"까지 완벽하게 설정됩니다.
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 직렬화 설정 (이건 완벽했습니다)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(false);

        return redisTemplate;
    }
}