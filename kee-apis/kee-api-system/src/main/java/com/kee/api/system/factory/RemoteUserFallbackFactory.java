package com.kee.api.system.factory;


import com.kee.api.system.RemoteUserService;
import com.kee.api.system.domain.SysUser;
import com.kee.api.system.model.UserInfo;
import com.kee.common.core.domain.R;
import com.kee.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 用户服务降级处理
 *
 * @author zms
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public R<UserInfo> getUserInfo(String username) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<UserInfo> phone(String phone) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult add(SysUser user, String type) {
                return AjaxResult.error("新增用户失败！！" + throwable.getMessage());
            }

            @Override
            public AjaxResult add(SysUser user) {
                return AjaxResult.error("新增用户失败！！" + throwable.getMessage());
            }

            @Override
            public R<SysUser> getSysUserByPhoneNumber(String phoneNumber) {
                return R.fail("根据手机号获取用户失败" + throwable.getMessage());
            }

            @Override
            public R<List<SysUser>> getSysUsersByPhoneNumber(String phoneNumber) {
                return R.fail("根据手机号获取多个用户失败"+throwable.getMessage());
            }

            @Override
            public R<Set<String>> getRoleKeysByUserId(Long userId) {
                return R.fail("获取当前用户角色失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult getInfoById(Long userId) {
                return AjaxResult.error("获取当前用户角色失败:" + throwable.getMessage());
            }

            @Override
            public AjaxResult getInfo() {
                return AjaxResult.error("获取用户信息（权限、角色）失败:" + throwable.getMessage());
            }


            @Override
            public AjaxResult getUserListByRoleKey(String roleKey) {
                return AjaxResult.error("根据角色权限标识查询用户Id失败："+throwable.getMessage());
            }
        };

    }
}
