/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.controller;
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

    public TecnicoBean() { super(Tecnico.class); }

    @Override
    public EntityManager getEntityManager() { return em; }


    public Integer tecnicoConMenosTickets() {
        String jpql =
                "SELECT t.id " +
                        "FROM Tecnico t " +
                        "LEFT JOIN Ticket tk ON tk.idTecnico = t " +
                        "WHERE t.activo = TRUE " +
                        "GROUP BY t.id " +
                        "ORDER BY COUNT(tk.id) ASC, t.id ASC ";

        var ids = em.createQuery(jpql, Integer.class)
                .setMaxResults(1)
                .getResultList();
        return ids.isEmpty() ? null : ids.get(0);
    }



    public int reasignarTodosLosTickets(Integer desdeId, Integer haciaId) {
        if (desdeId == null || haciaId == null) throw new IllegalArgumentException("IDs requeridos");
        if (desdeId.equals(haciaId)) throw new IllegalArgumentException("IDs no pueden ser iguales");

        var desde = em.getReference(Tecnico.class, desdeId);
        var hacia = em.getReference(Tecnico.class, haciaId);

        String jpql = "UPDATE Ticket t " +
                "SET t.idTecnico = :hacia, " +
                "    t.fechaAsignacion = COALESCE(t.fechaAsignacion, CURRENT_DATE) " +
                "WHERE t.idTecnico = :desde";

        int afectados = em.createQuery(jpql)
                .setParameter("hacia", hacia)
                .setParameter("desde", desde)
                .executeUpdate();

        em.clear(); // evitar entidades stale tras bulk update
        return afectados;
    }
}
