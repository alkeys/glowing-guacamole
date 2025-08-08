/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.control;

import com.in.nova.tech.entity.EstadosTicket;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@LocalBean
public class EstadosTicketBean extends AbstractDataPersistence<EstadosTicket> {

    @PersistenceContext(unitName = "JPA-EL-GUAPO-TOO")
    private EntityManager em;

    public EstadosTicketBean() {
        super(EstadosTicket.class);
    }

    @Override
    public jakarta.persistence.EntityManager getEntityManager() {
        return em;
    }
}
