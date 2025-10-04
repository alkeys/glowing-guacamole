/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.controller;
import java.util.List;

import com.in.nova.tech.entity.Tecnico;
import com.in.nova.tech.entity.Usuario;
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

    public Usuario findUsuarioById(Integer idUsuario) {
        String jpql= "SELECT u FROM Usuario u WHERE u.id = :idUsuario";
        return em.createQuery(jpql, Usuario.class)
                .setParameter("idUsuario", idUsuario)
                .getSingleResult();

    }




/**
 * Recupera una lista de técnicos activos ("Tecnico") que tienen la menor cantidad de tickets asignados,
 * opcionalmente filtrados por una especialidad dada.
 * <p>
 * El resultado se ordena primero por la cantidad de tickets asignados a cada técnico (ascendente),
 * y luego por el ID del técnico (ascendente) para desempatar.
 * </p>
 *
 * @param especialidad la especialidad por la cual filtrar los técnicos; si {@code null}, se incluyen todas las especialidades
 * @return una lista de técnicos activos con menos tickets, opcionalmente filtrados por especialidad
 */
    public List<Tecnico> tecnicosActivosConMenosTickets(String especialidad) {
        String jpql =
                "SELECT t " +
                        "FROM Tecnico t " +
                        "LEFT JOIN Ticket tk ON tk.idTecnico = t " +
                        "WHERE t.activo = TRUE " +
                        (especialidad != null ? "AND t.especialidad = :especialidad " : "") +
                        "GROUP BY t.id " +
                        "ORDER BY COUNT(tk.id) ASC, t.id ASC ";

        var query = em.createQuery(jpql, Tecnico.class);
        if (especialidad != null) {
            query.setParameter("especialidad", especialidad);
        }
        return query.getResultList();

    }



}
