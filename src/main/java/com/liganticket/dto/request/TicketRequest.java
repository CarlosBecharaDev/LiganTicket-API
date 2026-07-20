package com.liganticket.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record TicketRequest(

        @Schema(description = "Titulo del ticket", example = "Error al iniciar sesion")
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,

        @Schema(description = "Descripcion detallada del problema", example = "El sistema muestra error 500 al intentar loguearse")
        String descripcion,

        @Schema(description = "Nuevo estado del ticket (PENDIENTE, EN_PROCESO, COMPLETADO)", example = "EN_PROCESO")
        String estado
) {
}
