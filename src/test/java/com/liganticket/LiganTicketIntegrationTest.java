package com.liganticket;

import com.liganticket.dto.request.LoginRequest;
import com.liganticket.dto.request.RegistroRequest;
import com.liganticket.dto.request.TicketRequest;
import com.liganticket.dto.response.LoginResponse;
import com.liganticket.dto.response.TicketResponse;
import com.liganticket.dto.response.UsuarioResponse;
import com.liganticket.entity.enums.EstadoTicket;
import com.liganticket.exception.ErrorResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LiganTicketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String token;
    private static Long ticketId;

    // ---------- AUTENTICACION ----------

    @Test
    @Order(1)
    void registrarUsuario_exitoso() throws Exception {
        String body = """
                {
                    "nombre": "Test User",
                    "email": "test@liganticket.com",
                    "password": "test123"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@liganticket.com"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }

    @Test
    @Order(2)
    void registrarEmailDuplicado_conflicto() throws Exception {
        String body = """
                {
                    "nombre": "Otro User",
                    "email": "test@liganticket.com",
                    "password": "test123"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void login_exitoso() throws Exception {
        String body = """
                {
                    "email": "test@liganticket.com",
                    "password": "test123"
                }
                """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("test@liganticket.com"))
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @Order(4)
    void login_credencialesInvalidas() throws Exception {
        String body = """
                {
                    "email": "test@liganticket.com",
                    "password": "wrongpass"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    void obtenerPerfil_autenticado() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@liganticket.com"));
    }

    // ---------- TICKETS ----------

    @Test
    @Order(6)
    void crearTicket_exitoso() throws Exception {
        String body = """
                {
                    "titulo": "Bug en login",
                    "descripcion": "No funciona el boton de iniciar sesion"
                }
                """;

        String response = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Bug en login"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();

        ticketId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(7)
    void listarTickets_conDatos() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Order(8)
    void actualizarTicket_exitoso() throws Exception {
        String body = """
                {
                    "titulo": "Bug corregido en login",
                    "descripcion": "Actualizada la descripcion",
                    "estado": "EN_PROCESO"
                }
                """;

        mockMvc.perform(put("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    @Order(9)
    void eliminarTicket_exitoso() throws Exception {
        mockMvc.perform(delete("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    // ---------- SEGURIDAD ----------

    @Test
    @Order(10)
    void accesoSinToken_noAutorizado() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(11)
    void tokenInvalido_noAutorizado() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .header("Authorization", "Bearer token-invalido-123"))
                .andExpect(status().isUnauthorized());
    }
}
