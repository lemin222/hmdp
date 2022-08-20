package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendMessage(String phone, HttpSession session) {
        //1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            //2.失败返回
            return Result.fail("无效手机号");
        }
        //3.生成验证码
        String code = RandomUtil.randomNumbers(6);
        //4.保存在session中
        session.setAttribute("code", code);
        //5.发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);
        //返回
        return Result.ok();

    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            //2.失败返回
            return Result.fail("无效手机号");
        }
        //3.校验验证码
        String code = loginForm.getCode();
        String phoneCode = (String) session.getAttribute("code");
        if (code == null | phoneCode == null | !code.equals(phoneCode)) {
            //4.验证码失败返回
            return Result.fail("验证码错误");
        }
        //5.如果一致，根据手机号查询用户
        User user = this.baseMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));
        //6.不存在，创建新用户并保存
        if (user == null) {
            user = createUserWithPhone(phone);
        }
        //7.保存用户信息到session中
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        session.setAttribute("user", userDTO);

        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_" + RandomUtil.randomNumbers(10));
        this.baseMapper.insert(user);
        return user;
    }
}
