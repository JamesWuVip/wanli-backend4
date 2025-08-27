package com.wanli.security;

import com.wanli.entity.User;
import com.wanli.entity.UserStatus;
import com.wanli.exception.user.UserNotFoundException;
import com.wanli.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 * 
 * @author wanli
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
            .accountExpired(false)
            .accountLocked(user.isLocked())
            .credentialsExpired(false)
            .disabled(user.getStatus() != UserStatus.ACTIVE)
            .build();
    }
}