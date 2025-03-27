package com.example.personnel_management.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CollaborateurUserDetails implements UserDetails {
    private final Collaborateur collaborateur;

    public CollaborateurUserDetails(Collaborateur collaborateur) {
        this.collaborateur = collaborateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return collaborateur.getPassword();
    }

    @Override
    public String getUsername() {
        return collaborateur.getCin(); // Using CIN as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return collaborateur.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return collaborateur.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !collaborateur.isResetPassword();
    }

    @Override
    public boolean isEnabled() {
        return collaborateur.isActive();
    }

    public Collaborateur getCollaborateur() {
        return collaborateur;
    }
}