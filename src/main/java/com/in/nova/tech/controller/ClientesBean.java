/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.controller;


import com.in.nova.tech.entity.Cliente;
import com.in.nova.tech.entity.Usuario;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;

@Stateless
@LocalBean
public class ClientesBean extends AbstractDataPersistence<Cliente> implements Serializable {

    @PersistenceContext(unitName = "JPA-EL-GUAPO-TOO")
    private EntityManager em;

    public ClientesBean() {
        super(Cliente.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Usuario findUsuarioById(Integer idUsuario) {
        String jpql = "SELECT u FROM Usuario u WHERE u.id = :idUsuario";
        return em.createQuery(jpql, Usuario.class)
                .setParameter("idUsuario", idUsuario)
                .getSingleResult();
    }
}
