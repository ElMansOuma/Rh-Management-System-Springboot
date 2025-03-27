package com.example.personnel_management.service;

import com.example.personnel_management.model.Collaborateur;
import com.example.personnel_management.repository.CollaborateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollaborateurUserDetailsService implements UserDetailsService {

    private final CollaborateurRepository collaborateurRepository;

    @Override
    public UserDetails loadUserByUsername(String cin) throws UsernameNotFoundException {
        Collaborateur collaborateur = collaborateurRepository.findByCin(cin)
                .orElseThrow(() -> new UsernameNotFoundException("Collaborateur non trouvé avec le CIN : " + cin));

        return User
                .withUsername(collaborateur.getCin())
                .password(collaborateur.getPassword())
                .authorities("ROLE_ADMIN") // Use .authorities() with full role name
                .build();
    }

    // Keep the original method as a convenience method if needed
    public UserDetails loadUserByCin(String cin) throws UsernameNotFoundException {
        return loadUserByUsername(cin);
    }
}