package com.gestion.equipos.service;

import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene el usuario actual de la sesión
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String username = authentication.getName();
        return usuarioRepository.findByUsuario(username).orElse(null);
    }
    
    /**
     * Verifica si el usuario actual es admin
     */
    public boolean isCurrentUserAdmin() {
        Usuario currentUser = getCurrentUser();
        return currentUser != null && currentUser.getIsAdmin() != null && currentUser.getIsAdmin();
    }
}