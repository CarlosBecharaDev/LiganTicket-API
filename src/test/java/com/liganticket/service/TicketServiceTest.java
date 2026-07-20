package com.liganticket.service;

import com.liganticket.dto.request.TicketRequest;
import com.liganticket.dto.response.TicketResponse;
import com.liganticket.entity.Ticket;
import com.liganticket.entity.Usuario;
import com.liganticket.entity.enums.EstadoTicket;
import com.liganticket.entity.enums.Rol;
import com.liganticket.exception.EntityNotFoundException;
import com.liganticket.mapper.TicketMapper;
import com.liganticket.repository.TicketRepository;
import com.liganticket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketService ticketService;

    private Usuario crearUsuario() {
        return Usuario.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@liganticket.com")
                .password("hash")
                .rol(Rol.USER)
                .activo(true)
                .build();
    }

    private Ticket crearTicket(Usuario usuario) {
        return Ticket.builder()
                .id(1L)
                .titulo("Bug en login")
                .descripcion("No funciona el boton")
                .estado(EstadoTicket.PENDIENTE)
                .usuario(usuario)
                .creadoEn(LocalDateTime.now())
                .build();
    }

    // ---------- CREAR ----------

    @Test
    void crearTicket_exitoso() {
        // given
        Usuario usuario = crearUsuario();
        Ticket ticket = crearTicket(usuario);
        TicketRequest request = new TicketRequest("Bug en login", "No funciona el boton", null);

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toResponse(ticket)).thenReturn(
                new TicketResponse(1L, "Bug en login", "No funciona el boton",
                        EstadoTicket.PENDIENTE, "Test User", ticket.getCreadoEn()));

        // when
        TicketResponse response = ticketService.crear(request, "test@liganticket.com");

        // then
        assertNotNull(response);
        assertEquals("Bug en login", response.titulo());
        assertEquals(EstadoTicket.PENDIENTE, response.estado());

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        assertEquals("Bug en login", captor.getValue().getTitulo());
    }

    // ---------- LISTAR ----------

    @Test
    void listarTickets_exitoso() {
        // given
        Usuario usuario = crearUsuario();
        Ticket ticket = crearTicket(usuario);

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.findByUsuarioIdOrderByCreadoEnDesc(1L))
                .thenReturn(List.of(ticket));
        when(ticketMapper.toResponseList(List.of(ticket))).thenReturn(
                List.of(new TicketResponse(1L, "Bug en login", "No funciona",
                        EstadoTicket.PENDIENTE, "Test User", ticket.getCreadoEn())));

        // when
        List<TicketResponse> tickets = ticketService.listar("test@liganticket.com");

        // then
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        assertEquals("Bug en login", tickets.get(0).titulo());
    }

    // ---------- OBTENER ----------

    @Test
    void obtenerTicket_exitoso() {
        // given
        Usuario usuario = crearUsuario();
        Ticket ticket = crearTicket(usuario);

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(ticket));
        when(ticketMapper.toResponse(ticket)).thenReturn(
                new TicketResponse(1L, "Bug en login", "No funciona",
                        EstadoTicket.PENDIENTE, "Test User", ticket.getCreadoEn()));

        // when
        TicketResponse response = ticketService.obtener(1L, "test@liganticket.com");

        // then
        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void obtenerTicket_noEncontrado() {
        // given
        Usuario usuario = crearUsuario();
        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.findByIdAndUsuarioId(99L, 1L))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(EntityNotFoundException.class,
                () -> ticketService.obtener(99L, "test@liganticket.com"));
    }

    // ---------- ACTUALIZAR ----------

    @Test
    void actualizarTicket_exitoso() {
        // given
        Usuario usuario = crearUsuario();
        Ticket ticket = crearTicket(usuario);
        TicketRequest request = new TicketRequest("Actualizado", "Nueva desc", "EN_PROCESO");

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(ticket));
        when(ticketMapper.toResponse(ticket)).thenReturn(
                new TicketResponse(1L, "Actualizado", "Nueva desc",
                        EstadoTicket.EN_PROCESO, "Test User", ticket.getCreadoEn()));

        // when
        TicketResponse response = ticketService.actualizar(1L, request, "test@liganticket.com");

        // then
        assertEquals("Actualizado", response.titulo());
        assertEquals(EstadoTicket.EN_PROCESO, response.estado());
        assertEquals(EstadoTicket.EN_PROCESO, ticket.getEstado());
    }

    // ---------- ELIMINAR ----------

    @Test
    void eliminarTicket_exitoso() {
        // given
        Usuario usuario = crearUsuario();
        Ticket ticket = crearTicket(usuario);

        when(usuarioRepository.findByEmail("test@liganticket.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(ticket));

        // when
        ticketService.eliminar(1L, "test@liganticket.com");

        // then
        verify(ticketRepository).delete(ticket);
    }
}
