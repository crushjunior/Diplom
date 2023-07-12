package ru.skypro.homework.security;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ru.skypro.homework.dto.UserSecurity;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyUserDetails implements UserDetails {

    private UserSecurity userSecurity;

    public UserSecurity getUserSecurity() {
        return userSecurity;
    }

    public void setUserSecurity(UserSecurity userSecurity) {
        this.userSecurity = userSecurity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(userSecurity)
                .map(UserSecurity::getRole)
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .map(Collections::singleton)
                .orElseGet(Collections::emptySet);
    }

    @Override
    public String getPassword() {
        return Optional.ofNullable(userSecurity)
                .map(UserSecurity::getPassword)
                .orElse(null);
    }

    @Override
    public String getUsername() {
        return Optional.ofNullable(userSecurity)
                .map(UserSecurity::getEmail)
                .orElse(null);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}