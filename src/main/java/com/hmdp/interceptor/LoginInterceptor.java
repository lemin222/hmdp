package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.constant.RedisConstants;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: LoginInterceptor
 * @Description: TODO
 * @Author: fan
 * @Date: 2022/8/19 15:36
 * @Version 1.0
 **/
public class LoginInterceptor implements HandlerInterceptor {
    private StringRedisTemplate redisTemplate;

    public LoginInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.从请求头中获取token
        String token = request.getHeader("authorization");
        //2.获取redis中的用户
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token);
        //3.判断用户是否存在
        if (entries == null) {
            //4.不存在拦截
            //401授权异常
            response.setStatus(401);
            return false;
        }
        //5.存在，保存用户信息到ThreadLocal
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), false);
        UserHolder.saveUser(userDTO);
        //6.刷新redis时间
        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        //6.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除user
        UserHolder.removeUser();
    }
}