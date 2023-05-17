package com.kee.common.security.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 登录用户身份权限
 * 
 * @author zms
 */
public class LoginUser extends User
{
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Getter
    private Long userId;

    /**
     * 部门ID
     */
    @Getter
    private Long deptId;

    public LoginUser(Long userId, Long deptId, String username, String password, boolean enabled, boolean accountNonExpired,
                     boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities)
    {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.deptId = deptId;
    }
}
