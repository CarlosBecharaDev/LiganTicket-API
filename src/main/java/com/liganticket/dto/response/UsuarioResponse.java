package com.liganticket.dto.response;

import com.liganticket.entity.enums.Rol;

import java.time.LocalDateTime;


public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        Boolean activo,
        LocalDateTime creadoEn
) {
}
