package com.fire.usercenterdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fire.usercenterdemo.common.BaseResponse;
import com.fire.usercenterdemo.common.ErrorCode;
import com.fire.usercenterdemo.common.ResultUtils;
import com.fire.usercenterdemo.constant.UserConstant;
import com.fire.usercenterdemo.exception.BusinessException;
import com.fire.usercenterdemo.model.domain.User;
import com.fire.usercenterdemo.model.dto.user.UserLoginRequest;
import com.fire.usercenterdemo.model.dto.user.UserRegisterRequest;
import com.fire.usercenterdemo.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fire.usercenterdemo.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author tangfire
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    /**
     * 注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        long result =  userService.userRegister(userAccount, userPassword, checkPassword);

//        return new BaseResponse<>(0,result,"ok");
        return ResultUtils.success(result);
    }

    /**
     * 登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        User user = userService.userLogin(userAccount, userPassword, request);

//        return new BaseResponse<>(0,user,"ok");
        return ResultUtils.success(user);
    }


    /**
     * 查询用户
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {

        if (!isAdmin(request)){
//            return ResultUtils.error(ErrorCode.NO_AUTH);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<User> userList = userService.list(queryWrapper);
        List<User> list =  userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());

        return ResultUtils.success(list);
    }

    /**
     * 删除用户
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request) {

        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);

        }

        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);

        User user = (User) userObj;


        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);

        }

        long userId = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(userId);

        User safetyUser =  userService.getSafetyUser(user);

        return ResultUtils.success(safetyUser);




    }


    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }



}
