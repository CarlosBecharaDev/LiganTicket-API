package com.liganticket.service;

import com.liganticket.dto.request.LoginRequest;
import com.liganticket.dto.response.LoginResponse;
import com.liganticket.dto.response.UsuarioResponse;
import com.liganticket.entity.Usuario;
import com.liganticket.exception.EntityNotFoundException;
import com.liganticket.mapper.UsuarioMapper;
import com.liganticket.repository.UsuarioRepository;
import com.liganticket.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UsuarioRepository usuarioRepository,
                       UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        String token = jwtService.generateToken(usuario);

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRol(),
                jwtService.getExpiration()
        );
    }

    public UsuarioResponse obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return usuarioMapper.toResponse(usuario);
    }
}
