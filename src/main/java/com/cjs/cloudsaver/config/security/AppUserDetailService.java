package com.cjs.cloudsaver.config.security;

import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.service.account.AccountUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AppUserDetailService implements UserDetailsService {

    private final AccountUserService accountUserService;

    public AppUserDetailService(AccountUserService accountUserService) {
        this.accountUserService = accountUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUser user = accountUserService.findByLoginName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new AppUserDetails(user);
    }



}
