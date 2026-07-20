# LiganTicket API

API REST para gestion de tickets desarrollada con Spring Boot 3 y Java 21.

## Tecnologias

- **Spring Boot 3.4** - Framework principal
- **Spring Security + JWT** - Autenticacion sin estado (stateless)
- **Spring Data JPA** - Acceso a base de datos
- **PostgreSQL** - Base de datos relacional
- **Swagger / OpenAPI** - Documentacion interactiva de la API

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
controller/   -> Recibe las peticiones HTTP, valida los datos de entrada
service/      -> Contiene la logica de negocio
repository/   -> Se comunica con la base de datos (Spring Data JPA)
entity/       -> Representa las tablas de la base de datos como clases Java
dto/          -> Objetos para transferir datos entre capas (request/response)
security/     -> Configuracion de JWT y filtros de autenticacion
config/       -> Configuracion de Spring Security
exception/    -> Manejo global de errores
```

## Endpoints

### Usuarios (publico)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/usuarios` | Registrar un nuevo usuario |

### Autenticacion (publico)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/auth/login` | Iniciar sesion (devuelve token JWT) |

### Autenticacion (requiere token)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/auth/me` | Obtener perfil del usuario autenticado |

### Tickets (requiere token)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/tickets` | Listar todos mis tickets |
| POST | `/api/tickets` | Crear un ticket |
| GET | `/api/tickets/{id}` | Obtener un ticket por ID |
| PUT | `/api/tickets/{id}` | Actualizar un ticket |
| DELETE | `/api/tickets/{id}` | Eliminar un ticket |

## Como ejecutar

1. Tener PostgreSQL corriendo en localhost:5432
2. Crear la base de datos `ligan_ticket_db`
3. Ejecutar:

```bash
mvn spring-boot:run
```

Swagger disponible en: http://localhost:8080/swagger-ui.html

## Variables de entorno

| Variable | Default | Descripcion |
|----------|---------|-------------|
| DB_HOST | localhost | Host de PostgreSQL |
| DB_PORT | 5432 | Puerto de PostgreSQL |
| DB_NAME | ligan_ticket_db | Nombre de la base de datos |
| DB_USER | ligan_user | Usuario de PostgreSQL |
| DB_PASSWORD | ligan_pass | Contrasena de PostgreSQL |
| JWT_SECRET | (clave por defecto) | Clave para firmar tokens JWT |

## Flujo de uso

1. **Registrarse:** `POST /api/usuarios` con nombre, email y password
2. **Iniciar sesion:** `POST /api/auth/login` con email y password -> obtienes un token JWT
3. **Usar el token:** En cada peticion, enviar header `Authorization: Bearer <token>`
4. **Gestionar tickets:** CRUD completo en `/api/tickets`
