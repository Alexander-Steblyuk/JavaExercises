package ru.steblyuk.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
public class JpaGenreRepository implements GenreRepository {
    private static final String IDS_PARAM_NAME = "ids";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("select g from Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return entityManager.createQuery("select g from Genre g where g.id in (:ids)", Genre.class)
                .setParameter(IDS_PARAM_NAME, ids)
                .getResultList();
    }
}
