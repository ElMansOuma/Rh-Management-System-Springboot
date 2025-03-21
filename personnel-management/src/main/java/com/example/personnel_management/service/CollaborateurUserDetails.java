package com.example.personnel_management.service;

import com.example.personnel_management.model.Collaborateur;
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
        // Vous pouvez ajouter des rôles/autorisations spécifiques ici
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_COLLABORATEUR"));
    }

    @Override
    public String getPassword() {
        return collaborateur.getPassword();
    }

    @Override
    public String getUsername() {
        // Utiliser le CIN comme nom d'utilisateur
        return collaborateur.getCin();
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