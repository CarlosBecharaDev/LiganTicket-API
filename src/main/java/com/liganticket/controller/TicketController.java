package com.liganticket.controller;

import com.liganticket.dto.request.TicketRequest;
import com.liganticket.dto.response.TicketResponse;
import com.liganticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "CRUD de tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "Listar mis tickets", description = "Devuelve todos los tickets del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tickets"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<TicketResponse>> listar(Authentication authentication) {
        return ResponseEntity.ok(ticketService.listar(authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "Crear ticket", description = "Crea un nuevo ticket para el usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket creado",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<TicketResponse> crear(@Valid @RequestBody TicketRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.crear(request, authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket", description = "Obtiene un ticket por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<TicketResponse> obtener(@PathVariable Long id,
                                                   Authentication authentication) {
        return ResponseEntity.ok(ticketService.obtener(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ticket", description = "Actualiza un ticket existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket actualizado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<TicketResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody TicketRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(ticketService.actualizar(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ticket", description = "Elimina un ticket por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ticket eliminado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                          Authentication authentication) {
        ticketService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
