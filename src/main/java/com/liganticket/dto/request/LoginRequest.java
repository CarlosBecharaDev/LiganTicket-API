package com.liganticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginRequest(

        @Schema(description = "Email del usuario", example = "admin@liganticket.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        String email,

        @Schema(description = "Contrasena del usuario", example = "admin123")
        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
