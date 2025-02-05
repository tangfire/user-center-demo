package com.fire.usercenterdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fire.usercenterdemo.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Admin
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-02-05 21:32:58
*/
public interface UserService extends IService<User> {



    /**
     * 用户注释
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);
}
