package com.liganticket.service;

import com.liganticket.dto.request.TicketRequest;
import com.liganticket.dto.response.TicketResponse;
import com.liganticket.entity.Ticket;
import com.liganticket.entity.Usuario;
import com.liganticket.entity.enums.EstadoTicket;
import com.liganticket.exception.EntityNotFoundException;
import com.liganticket.mapper.TicketMapper;
import com.liganticket.repository.TicketRepository;
import com.liganticket.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final TicketMapper ticketMapper;

    public TicketService(TicketRepository ticketRepository,
                         UsuarioRepository usuarioRepository,
                         TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listar(String email) {
        Usuario usuario = getUsuario(email);
        List<Ticket> tickets = ticketRepository.findByUsuarioIdOrderByCreadoEnDesc(usuario.getId());
        return ticketMapper.toResponseList(tickets);
    }

    public TicketResponse crear(TicketRequest request, String email) {
        Usuario usuario = getUsuario(email);

        Ticket ticket = Ticket.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .usuario(usuario)
                .build();

        if (request.estado() != null && !request.estado().isBlank()) {
            ticket.setEstado(EstadoTicket.valueOf(request.estado().toUpperCase()));
        }

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public TicketResponse obtener(Long id, String email) {
        Usuario usuario = getUsuario(email);
        Ticket ticket = ticketRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con id: " + id));
        return ticketMapper.toResponse(ticket);
    }

    public TicketResponse actualizar(Long id, TicketRequest request, String email) {
        Usuario usuario = getUsuario(email);
        Ticket ticket = ticketRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con id: " + id));

        ticket.setTitulo(request.titulo());
        ticket.setDescripcion(request.descripcion());

        if (request.estado() != null && !request.estado().isBlank()) {
            ticket.setEstado(EstadoTicket.valueOf(request.estado().toUpperCase()));
        }

        return ticketMapper.toResponse(ticket);
    }

    public void eliminar(Long id, String email) {
        Usuario usuario = getUsuario(email);
        Ticket ticket = ticketRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con id: " + id));
        ticketRepository.delete(ticket);
    }

    private Usuario getUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }
}
