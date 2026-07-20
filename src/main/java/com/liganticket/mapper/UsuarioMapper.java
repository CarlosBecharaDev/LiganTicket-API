package com.liganticket.mapper;

import com.liganticket.dto.response.UsuarioResponse;
import com.liganticket.entity.Usuario;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);

    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
}
