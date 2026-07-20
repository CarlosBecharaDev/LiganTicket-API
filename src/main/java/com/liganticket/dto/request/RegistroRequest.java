package com.liganticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record RegistroRequest(

        @Schema(description = "Nombre completo del usuario", example = "Juan Perez")
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Schema(description = "Email del usuario, debe ser unico", example = "juan@liganticket.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        String email,

        @Schema(description = "Contrasena del usuario", example = "miPassword123")
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String password
) {
}
