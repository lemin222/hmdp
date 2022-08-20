package com.hmdp;

import com.hmdp.constant.RedisConstants;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@SpringBootTest
class HmDianPingApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void test() {
        Map<Object, Object> entries1 = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + 932624);
        System.out.println(entries1);
        Map<Object, Object> token = redisTemplate.opsForHash().entries("token");
        if (token.isEmpty()) {
            System.out.println("token为null");
        }
        System.out.println("token为：" + token);
    }


}
