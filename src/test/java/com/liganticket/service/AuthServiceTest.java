package com.liganticket.service;

import com.liganticket.dto.request.LoginRequest;
import com.liganticket.dto.request.RegistroRequest;
import com.liganticket.dto.response.LoginResponse;
import com.liganticket.dto.response.UsuarioResponse;
import com.liganticket.entity.Usuario;
import com.liganticket.entity.enums.Rol;
import com.liganticket.exception.EmailAlreadyExistsException;
import com.liganticket.mapper.UsuarioMapper;
import com.liganticket.repository.UsuarioRepository;
import com.liganticket.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthService authService;

    private Usuario crearUsuario() {
        return Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@liganticket.com")
                .password("hash123")
                .rol(Rol.USER)
                .activo(true)
                .build();
    }

    // ---------- LOGIN ----------

    @Test
    void login_exitoso() {
        // given
        String tokenEsperado = "jwt-token-123";
        Usuario usuario = crearUsuario();
        LoginRequest request = new LoginRequest("test@liganticket.com", "test123");

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn(tokenEsperado);
        when(jwtService.getExpiration()).thenReturn(86400000L);

        // when
        LoginResponse response = authService.login(request);

        // then
        assertNotNull(response);
        assertEquals(tokenEsperado, response.token());
        assertEquals("test@liganticket.com", response.email());
        assertEquals(Rol.USER, response.rol());
    }

    @Test
    void login_credencialesInvalidas() {
        // given
        LoginRequest request = new LoginRequest("test@liganticket.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        // when / then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    // ---------- PERFIL ----------

    @Test
    void obtenerPerfil_exitoso() {
        // given
        Usuario usuario = crearUsuario();
        UsuarioResponse responseEsperada = new UsuarioResponse(
                1L, "Test User", "test@liganticket.com", Rol.USER, true, null);

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(responseEsperada);

        // when
        UsuarioResponse response = authService.obtenerPerfil("test@liganticket.com");

        // then
        assertNotNull(response);
        assertEquals("test@liganticket.com", response.email());
    }
}
