package com.liganticket.mapper;

import com.liganticket.dto.response.TicketResponse;
import com.liganticket.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "usuarioNombre", source = "usuario.nombre")
    TicketResponse toResponse(Ticket ticket);

    List<TicketResponse> toResponseList(List<Ticket> tickets);
}
