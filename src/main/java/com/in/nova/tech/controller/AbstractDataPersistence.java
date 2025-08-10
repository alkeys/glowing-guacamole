/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/


package com.in.nova.tech.controller;

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
     <p>El método `create` persiste una nueva entidad en la base de datos usando JPA.
     Primero valida que la entidad no sea nula. Luego obtiene un `EntityManager` válido y llama a `persist(entity)`
     para marcar la entidad como nueva y gestionada. Después, ejecuta `flush()` para sincronizar los cambios con la base de datos inmediatamente.
     Si ocurre algún error, lanza una excepción con un mensaje descriptivo.</p>
     * @param entity Entidad a persistir.
     */
    public void create(final T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entidad nula.");
        }

        try {
            EntityManager em = requireEntityManager();
            em.persist(entity);
            em.flush();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al persistir la entidad.", ex);
        }
    }


    /**
     <p>El método `findById` busca y retorna una entidad de tipo `T` usando su identificador (`id`). Primero valida que el `id` no sea nulo,
     lanzando una excepción si lo es. Luego, intenta obtener la entidad usando el método `find` del `EntityManager`, pasando la clase de
     la entidad (`tipoDato`) y el `id`. Si ocurre algún error durante la búsqueda, lanza una excepción con un mensaje descriptivo.</p>
     * @param id Identificador de la entidad a buscar.
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
      <p>La función `delete` elimina una entidad de la base de datos usando JPA. Primero verifica que la entidad no sea nula.
      Luego obtiene un `EntityManager` válido, asegura que la entidad esté gestionada mediante `merge`, y finalmente la elimina con `remove`.
      Si ocurre un error, lanza una excepción con un mensaje descriptivo.</p>
        * @param entity Entidad a eliminar.
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
     <p> El método `update` actualiza una entidad en la base de datos usando JPA. Primero verifica que la entidad no sea nula,
     lanzando una excepción si lo es. Luego, intenta fusionar la entidad con el contexto de persistencia usando `merge`,
     lo que devuelve la entidad gestionada. Después, llama a `flush()` para sincronizar los cambios con la base de datos
     inmediatamente (esto es opcional). Si ocurre algún error, lanza una excepción con un mensaje descriptivo.</p>
     * @param entity Entidad a actualizar.
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
     <p>El método `findRange` implementa paginación usando JPA Criteria API. Recibe dos parámetros: `first` (índice inicial, base 0) y
     `pageSize` (cantidad de resultados). Valida que ambos sean válidos, luego construye una consulta para la entidad genérica `T`,
     y usa `setFirstResult` y `setMaxResults` para limitar los resultados devueltos. Si ocurre un error, lanza una excepción.
     Devuelve una lista de entidades del rango solicitado.</p>
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
     <p>El método `count()` cuenta el número total de entidades del tipo `T` en la base de datos.
     Utiliza la Criteria API de JPA para construir una consulta que realiza un `SELECT COUNT` sobre la entidad.
     Ejecuta la consulta y retorna el resultado como un entero. Si ocurre un error, lanza una excepción con un mensaje descriptivo.</p>
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


    /**
     <p>El método `findAll()` recupera todas las entidades del tipo `T` desde la base de datos usando la Criteria API de JPA.

     1. Obtiene un `EntityManager` válido.
     2. Usa `CriteriaBuilder` para crear una consulta (`CriteriaQuery`) del tipo de entidad.
     3. Define el origen de la consulta (`Root<T>`).
     4. Selecciona todos los registros (`cq.select(root)`).
     5. Crea y ejecuta la consulta (`TypedQuery<T>`), devolviendo la lista de resultados.
     6. Si ocurre un error, lanza una excepción con un mensaje descriptivo.

     Este método es genérico y funciona para cualquier entidad JPA gestionada por la clase.
     </p>
     * @return
     */
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
