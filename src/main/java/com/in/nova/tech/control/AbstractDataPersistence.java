/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/


package com.in.nova.tech.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Clase abstracta genérica para operaciones CRUD usando JPA.
 * @param <T> Tipo de entidad manejada.
 */
public abstract class AbstractDataPersistence<T> {

    public abstract EntityManager getEntityManager();

    private final Class<T> tipoDato;

    public AbstractDataPersistence(Class<T> tipoDato) {
        this.tipoDato = tipoDato;
    }

    /**
     * Verifica y retorna un EntityManager válido.
     * @return EntityManager
     * @throws IllegalStateException si es nulo.
     */
    private EntityManager requireEntityManager() {
        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio: EntityManager es nulo.");
        }
        return em;
    }

    /**
     * Crea una nueva entidad en la base de datos.
     */
    public void create(final T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entidad nula.");
        }

        try {
            EntityManager em = requireEntityManager();
            em.persist(entity);
            em.flush(); // 👈 fuerza sincronización inmediata con la base
        } catch (Exception ex) {
            throw new IllegalStateException("Error al persistir la entidad.", ex);
        }
    }


    /**
     * Busca una entidad por su ID.
     */
    public T findById(final Object id) {
        if (id == null) {
            throw new IllegalArgumentException("Parámetro no válido: ID es null.");
        }

        try {
            return requireEntityManager().find(tipoDato, id);
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar la entidad por ID.", ex);
        }
    }

    /**
     * Elimina una entidad de la base de datos.
     */
    public void delete(final T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Parámetro no válido: entidad es null.");
        }

        try {
            EntityManager em = requireEntityManager();
            T managedEntity = em.merge(entity); // Asegura que esté en estado gestionado
            em.remove(managedEntity);
        } catch (Exception ex) {
            throw new IllegalStateException("Error al eliminar la entidad.", ex);
        }
    }

    /**
     * Actualiza una entidad en la base de datos.
     */
    public T update(final T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Parámetro no válido: entidad es null.");
        }

        try {
            T merged = requireEntityManager().merge(entity);
            requireEntityManager().flush(); // 👈 opcional
            return merged;
        } catch (Exception ex) {
            throw new IllegalStateException("Error al actualizar la entidad.", ex);
        }
    }


    /**
     * Retorna una lista de entidades en un rango (paginación).
     */
    public List<T> findRange(int first, int pageSize) {
        if (first < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Parámetros no válidos: first < 0 o pageSize <= 0.");
        }

        try {
            EntityManager em = requireEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(tipoDato);
            Root<T> root = cq.from(tipoDato);
            cq.select(root);

            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(first);
            query.setMaxResults(pageSize);

            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();  // solo durante desarrollo
            throw new IllegalStateException("Error al obtener el rango de entidades: " + ex.getMessage(), ex);
        }

    }

    /**
     * Cuenta el total de entidades almacenadas.
     */
    public int count() {
        try {
            EntityManager em = requireEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(tipoDato);
            cq.select(cb.count(root));

            TypedQuery<Long> query = em.createQuery(cq);
            return query.getSingleResult().intValue();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar las entidades.", ex);
        }
    }

    public List<T> findAll() {
        try {
            EntityManager em = requireEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(tipoDato);
            Root<T> root = cq.from(tipoDato);
            cq.select(root);

            TypedQuery<T> query = em.createQuery(cq);
            return query.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al obtener todas las entidades.", ex);
        }
    }
}
