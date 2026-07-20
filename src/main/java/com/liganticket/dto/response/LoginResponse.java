package com.liganticket.dto.response;

import com.liganticket.entity.enums.Rol;


public record LoginResponse(
        String token,
        String tipo,
        String email,
        Rol rol,
        long expiraEnMs
) {
}
