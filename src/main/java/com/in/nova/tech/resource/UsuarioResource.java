package com.in.nova.tech.resource;
import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.UsuarioBean;
import com.in.nova.tech.dto.UsuarioDto;
import com.in.nova.tech.entity.Usuario;
import com.in.nova.tech.utils.PasswordHashSeguro;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import static com.in.nova.tech.utils.PasswordHashSeguro.hashPassword;


@Path("/usuarios")
@Tags(value = {
        @Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios, incluyendo CRUD y gestión de datos.")
})
public class UsuarioResource extends AbstractCrudResource<Usuario, UsuarioDto, Integer> {

    @Inject
    private UsuarioBean usuarioBean;


    @Override
    protected AbstractDataPersistence<Usuario> getService() {
        return usuarioBean;
    }

    @Override
    protected Integer getId(Usuario entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Usuario entity, Integer integer) {
    }

    @Override
    protected UsuarioDto toDto(Usuario entity) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(entity.getId());
        dto.setNombreUsuario(entity.getNombreUsuario());

        dto.setRol(entity.getRol());
        try {
            dto.setIdUsuario(entity.getCliente().getIdUsuario().getId());
            dto.setNombreCompleto(entity.getCliente().getNombreCompleto());
        } catch (Exception e) {
            dto.setIdUsuario(null);
            dto.setNombreCompleto(null);
        }
        try {
            dto.setIdTecnico(entity.getTecnico().getId());
            dto.setNombreCompleto(entity.getTecnico().getNombreCompleto());
        } catch (Exception e) {
            dto.setIdTecnico(null);
            dto.setNombreCompleto(null);
        }

        return dto;
    }

    @Override
    protected Usuario toEntity(UsuarioDto dto) {
        Usuario entity = new Usuario();
        entity.setId(dto.getId());
        entity.setNombreUsuario(dto.getNombreUsuario());
        //incrita la contrasenaHash primero
        if (dto.getContrasena() == null || dto.getContrasena().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (dto.getContrasena().length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (dto.getContrasena().length() > 64) {
            throw new IllegalArgumentException("La contraseña no puede tener más de 64 caracteres");
        }
        String hash = hashPassword(dto.getContrasena());
        entity.setContrasenaHash(hash);
        entity.setRol(dto.getRol());
        return entity;
    }



    @GET
    @Path("/Login/{nombreUSer}/{contrasena}")
    public Response login(@PathParam("nombreUSer") String nombreUser,@PathParam("contrasena") String contrasena) {
        Usuario usuario = usuarioBean.findByNombreUsuario(nombreUser);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Usuario no encontrado\"}")
                    .build();
        }
        // Verificar la contraseña
        String hashed = usuario.getContrasenaHash();
        return PasswordHashSeguro.checkPassword(contrasena, hashed) ?
                Response.ok(toDto(usuario)).build() :
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Contraseña incorrecta\"}")
                        .build();
    }




}
