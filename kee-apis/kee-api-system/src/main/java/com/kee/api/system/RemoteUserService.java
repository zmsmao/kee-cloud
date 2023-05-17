package com.kee.api.system;

import com.kee.api.system.domain.SysUser;
import com.kee.api.system.factory.RemoteUserFallbackFactory;
import com.kee.api.system.model.UserInfo;
import com.kee.common.core.constant.ServiceNameConstants;

import com.kee.common.core.domain.R;
import com.kee.common.core.web.domain.AjaxResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 用户服务
 * 
 * @author zms
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    @GetMapping(value = "/user/info/{username}")
    R<UserInfo> getUserInfo(@PathVariable("username") String username);

    /**
     * 通过手机号码查询用户
     *
     *
     * @param phone@return 用户对象信息
     */
    @GetMapping(value = "/user/code/phone")
    R<UserInfo> phone(@RequestParam("phone") String phone);
    /**
     * 新增用户
     * @param user
     * @return
     */
    @PostMapping(value = "/user/add")
    AjaxResult add(@RequestBody SysUser user, @RequestParam("type") String type);

    @PostMapping(value = "/user")
    AjaxResult add(@RequestBody SysUser user);

    /**
     * 根据手机号获取用户信息
     */
    @GetMapping( value = "/user/getSysUserByPhoneNumber" )
    R<SysUser> getSysUserByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber);

    /**
     * 根据手机号获取多个用户信息
     */
    @GetMapping(value = "/user/getSysUsersByPhoneNumber")
    R<List<SysUser>> getSysUsersByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber);

    /**
     * 通过用户id查询用户信息
     *
     * @param userId 用户名
     * @return 结果
     */
    @GetMapping(value = "/user/getInfoById")
    AjaxResult getInfoById(@RequestParam("userId") Long userId);


    /**
     * 获取用户信息（权限、角色）
     * @return
     */
    @GetMapping("/user/getInfo")
    AjaxResult getInfo();

    /**
     * 根据用户编号获取角色权限列表
     */
    @ApiOperation(value = "根据用户编号获取角色权限列表")
    @GetMapping("/user/getRoleKeys/{userId}")
    R<Set<String>> getRoleKeysByUserId(@PathVariable("userId") Long userId);


    @GetMapping("/user/roleKey")
    AjaxResult getUserListByRoleKey(@RequestParam("roleKey") String roleKey);

}
