package com.liganticket.dto.response;

import com.liganticket.entity.enums.EstadoTicket;

import java.time.LocalDateTime;


public record TicketResponse(
        Long id,
        String titulo,
        String descripcion,
        EstadoTicket estado,
        String usuarioNombre,
        LocalDateTime creadoEn
) {
}
