package com.javanauta.agendadortarefas.infrastructure.security;

import com.javanauta.agendadortarefas.business.dto.UsuarioDTO;
import com.javanauta.agendadortarefas.infrastructure.client.UsuarioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl {

    private final UsuarioClient client;

    public UserDetails carregaDadosUsuario(String email, String token) {
        String bearerToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        UsuarioDTO usuarioDTO = client.buscaUsuarioPorEmail(email, bearerToken);

        return User
                .withUsername(usuarioDTO.getEmail())
                .password("")
                .authorities("USER")
                .build();
    }
}