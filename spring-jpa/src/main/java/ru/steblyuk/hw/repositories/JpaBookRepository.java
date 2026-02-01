package ru.steblyuk.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookRepository implements BookRepository {
    private static final String ID_PARAM_NAME = "id";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        return entityManager.createQuery("select b from Book b join fetch b.author join fetch b.genres " +
                        "where b.id = :id", Book.class)
                .setParameter(ID_PARAM_NAME, id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("select b from Book b join fetch b.author", Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
       findById(id).ifPresent(entityManager::remove);
    }
}
