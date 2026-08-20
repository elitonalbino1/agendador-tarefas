package com.javanauta.agendadortarefas.infrastructure.security;

import com.javanauta.agendadortarefas.business.dto.UsuarioDTO;
import com.javanauta.agendadortarefas.infrastructure.client.UsuarioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl  {

    @Autowired
    private UsuarioClient client;

    public UserDetails carregaDadosUsuario(String email, String token) {
        // Garante que o header Authorization esteja no formato correto
        String bearerToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        // Chama o serviço Usuário via Feign
        UsuarioDTO usuarioDTO = client.buscaUsuarioPorEmail(email, bearerToken);

        return User
                .withUsername(usuarioDTO.getEmail()) // usa o email como username
                .password("") // não precisa da senha
                .authorities("USER") // define uma role padrão
                .build();
    }
}
