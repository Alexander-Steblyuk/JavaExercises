package ru.steblyuk.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCommentRepository implements CommentRepository {
    private static final String ID_PARAM_NAME = "id";

    @PersistenceContext
    private EntityManager entityManager;

    @EntityGraph(attributePaths = "book")
    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public List<Comment> findByBookId(long id) {
        return entityManager.createQuery("select c from Comment c where c.book.id = :id", Comment.class)
                .setParameter(ID_PARAM_NAME, id)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        return entityManager.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        findById(id).ifPresent(entityManager::remove);
    }
}
