package ru.steblyuk.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAuthorRepository implements AuthorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Author> findAll() {
        return entityManager.createQuery("select a from Author a", Author.class).getResultList();
    }

    @Override
    public Optional<Author> findById(long id) {
        Author author = entityManager.find(Author.class, id);
        return Optional.ofNullable(author);
    }
}
