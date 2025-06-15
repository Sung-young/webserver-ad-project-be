package com.mango.diary.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisDao {

    private final RedisTemplate<String, String> redisTemplate;

    private final String SIGN_OUT_VALUE = "SIGN_OUT_VALUE";

    public void setRefreshToken(String userId, String refreshToken, Long refreshTokenTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.opsForValue().set(userId, refreshToken, refreshTokenTime, TimeUnit.SECONDS);
    }

    public void setVerificationCode(String userEmail, String verificationCode, Long verificationCodeTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.opsForValue().set(userEmail, verificationCode, verificationCodeTime, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String userEmail) {
        String verificationStatus = redisTemplate.opsForValue().get(userEmail);
        if(verificationStatus == null){
            return "Not Verified";
        }
        return verificationStatus;
    }

    public void setVerification(String userEmail, Long verificationTime) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.opsForValue().set(userEmail, "Verified", verificationTime, TimeUnit.MINUTES);
    }

    public String getVerification(String mail) {
        String verificationStatus = redisTemplate.opsForValue().get(mail);
        if(verificationStatus == null){
            return "Not Verified";
        }
        return verificationStatus;
    }

    public boolean isKeyOfAccessTokenInBlackList(String accessToken) {
        String signOutValue = redisTemplate.opsForValue().get(accessToken);
        return SIGN_OUT_VALUE.equals(signOutValue);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(userId);
    }

    public void setAccessTokenSignOut(String accessToken){
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.opsForValue().set(accessToken, SIGN_OUT_VALUE, 2L, TimeUnit.DAYS);
    }

    public void deleteVerification(String userEmail) {
        redisTemplate.delete(userEmail);
    }

    public String getRefreshToken(String userId){
        return redisTemplate.opsForValue().get(userId);
    }
}
