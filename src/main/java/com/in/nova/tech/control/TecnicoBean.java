/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.control;

import com.in.nova.tech.entity.Tecnico;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Stateless
@LocalBean
public class TecnicoBean extends AbstractDataPersistence<Tecnico> {

    @PersistenceContext(unitName = "JPA-EL-GUAPO-TOO")
    private EntityManager em;

    public TecnicoBean() {
        super(Tecnico.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
