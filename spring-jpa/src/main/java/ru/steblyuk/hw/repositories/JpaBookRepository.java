package ru.steblyuk.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JpaBookRepository implements BookRepository {
    private static final String BOOK_BY_ID_GRAPH_NAME = "book-by-id";
    private static final String ALL_BOOKS_GRAPH_NAME = "all-books";
    private static final String ENTITY_GRAPH_HINT_NAME = "javax.persistence.fetchgraph";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> graph = entityManager.getEntityGraph(BOOK_BY_ID_GRAPH_NAME);
        Map<String, Object> properties = Map.of(ENTITY_GRAPH_HINT_NAME, graph);
        return Optional.ofNullable(entityManager.find(Book.class, id, properties));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> graph = entityManager.getEntityGraph(ALL_BOOKS_GRAPH_NAME);
        return entityManager.createQuery("select b from Book b", Book.class)
                .setHint(ENTITY_GRAPH_HINT_NAME, graph)
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
