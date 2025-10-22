package com.gym.backend.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gym.backend.model.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // o roles si los tenés
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public LocalDateTime getLastLogout() {
        return user.getLastLogout();
    }

    public LocalDateTime getfechaRegistro() {
        return user.getFechaRegistro();
    }

    @Override
    public boolean isEnabled() {
        return user.isActivo();
    }
}
