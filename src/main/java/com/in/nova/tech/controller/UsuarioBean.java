package com.in.nova.tech.controller;

import com.in.nova.tech.entity.Usuario;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;

@Stateless
@LocalBean
public class UsuarioBean extends AbstractDataPersistence<Usuario>  implements Serializable {

    @PersistenceContext(unitName = "JPA-EL-GUAPO-TOO")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public UsuarioBean() {
        super(Usuario.class);
    }

    public Usuario findByNombreUsuario(String nombreUsuario) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario", Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // o lanzar una excepción personalizada si es necesario
        }
    }
}
