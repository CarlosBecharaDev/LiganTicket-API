package com.liganticket.repository;

import com.liganticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    Optional<Ticket> findByIdAndUsuarioId(Long id, Long usuarioId);
}
