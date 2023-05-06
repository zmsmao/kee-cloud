package com.kee.model.system.controller;


import com.kee.api.system.domain.SysRole;
import com.kee.api.system.domain.SysUser;
import com.kee.api.system.model.UserInfo;
import com.kee.common.core.constant.UserConstants;
import com.kee.common.core.domain.R;
import com.kee.common.core.utils.StringUtils;
import com.kee.common.core.utils.poi.ExcelUtil;
import com.kee.common.core.web.controller.BaseController;
import com.kee.common.core.web.domain.AjaxResult;
import com.kee.common.core.web.page.TableDataInfo;
import com.kee.common.log.annotation.Log;
import com.kee.common.log.enums.BusinessType;
import com.kee.common.security.utils.SecurityUtils;
import com.kee.model.system.service.ISysPermissionService;
import com.kee.model.system.service.ISysRoleService;
import com.kee.model.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户信息
 * 
 * @author zms
 */
@Validated
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

//    @Autowired
//    private ISysPostService postService;

    @Autowired
    private ISysPermissionService permissionService;

    /**
     * 获取用户列表
     */
    //@PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/getUserByPhone")
    public AjaxResult getUserByPhone(String phoneNumber)
    {
//        SysUser user = userService.selectUserByPhone(phoneNumber);
        return AjaxResult.success(null);
    }

    /**
     * 获取用户列表
     */
    //@PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/getList")
    public TableDataInfo getList()
    {
        startPage();
        List<SysUser> list = new ArrayList<>();
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) throws IOException
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{username}")
    public R<UserInfo> info(@PathVariable("username") String username)
    {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (StringUtils.isNull(sysUser))
        {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        UserInfo sysUserVo = new UserInfo();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(new SysUser());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(new SysUser());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", userService.selectUserById(userId));
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
//        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
//            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 根据用户ID获取详细信息
     */
    @GetMapping("/getInfoById")
    public AjaxResult getInfoById(@RequestParam("userId") Long userId)
    {
        SysUser user = userService.selectUserById(userId);
        return AjaxResult.success(user);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
//        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName())))
//        {
//            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
//        }
//        else
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 修改用户
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public AjaxResult update(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(1);
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
//        user.setCheckTime(DateUtils.getNowDate());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 重置密码（无权限）
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPassword")
    public AjaxResult resetPassword(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
//        user.setCheckTime(DateUtils.getNowDate());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户id获取微信openid
     */
    @GetMapping("/getOpenId")
    public AjaxResult getOpenIdByUserId(@RequestParam(value = "userId") Long userId)
    {
        SysUser sysUser = userService.selectUserById(userId);
        return AjaxResult.success();
    }

    @GetMapping("/ids")
    public AjaxResult getUserByIds(@RequestParam boolean simple,@RequestParam Long[] userIds){
        return AjaxResult.success();
    }

    /**
     * 获取用户密码是否过期
     * @param userId
     * @return
     */
    @GetMapping("/passwordMsg")
    public AjaxResult passwordMsg(@RequestParam("userId") Long userId){
        return AjaxResult.success();
    }

    /**
     * 设置用户的部门ID
     *
     * @creator cai.haoming @ 2023/3/6
     * @since 1.0.0-SNAPSHOT
     */
    @PutMapping("/setUserDept")
    @ApiOperation("设置用户部门ID")
    public AjaxResult setUserDept(@ApiParam("部门ID") @NotNull(message = "请选择部门") Long deptId,
            @ApiParam("用户IDs") @NotNull(message = "请选择用户") Long[] userIds) {
//        userService.updateUserByDeptId(deptId, userIds);
        return AjaxResult.success();
    }

    /**
     * 根据角色ID查用户
     *
     * @creator cai.haoming @ 2023/3/6
     * @since 1.0.0-SNAPSHOT
     */
    @GetMapping("/getUserByRoleId")
    @ApiOperation("根据角色ID查用户")
    public AjaxResult getUserByRoleId(@ApiParam("角色ID") @NotNull(message = "请选择角色") Long roleId) {
        return AjaxResult.success();
    }

    /**
     * 关联角色与用户
     *
     * @creator cai.haoming @ 2023/3/6
     * @since 1.0.0-SNAPSHOT
     */
    @PostMapping("/setUserRole")
    @ApiOperation("管理用户与角色 角色-用户 一对多")
    public AjaxResult setUserRole(@ApiParam("角色ID") @NotNull(message = "请选择角色") Long roleId,
            @ApiParam("用户IDs") @NotNull(message = "请选择用户") Long[] userIds) {
//        userService.insertUserRole(roleId, userIds);
        return AjaxResult.success();
    }

}
