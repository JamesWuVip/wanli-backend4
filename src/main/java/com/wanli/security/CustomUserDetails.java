package com.wanli.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * 自定义用户详情类
 * 扩展Spring Security的User类，添加用户ID信息
 */
@Getter
public class CustomUserDetails extends User {
    
    private final UUID userId;
    
    public CustomUserDetails(String username, String password, 
                           Collection<? extends GrantedAuthority> authorities,
                           UUID userId) {
        super(username, password, authorities);
        this.userId = userId;
    }
    
    public CustomUserDetails(String username, String password, 
                           boolean enabled, boolean accountNonExpired,
                           boolean credentialsNonExpired, boolean accountNonLocked,
                           Collection<? extends GrantedAuthority> authorities,
                           UUID userId) {
        super(username, password, enabled, accountNonExpired, 
              credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }
}