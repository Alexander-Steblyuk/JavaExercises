package ru.steblyuk.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author"})
    @NonNull
    @Override
    List<Book> findAll();

    @EntityGraph(attributePaths = {"author", "genres"})
    @NonNull
    @Override
    Optional<Book> findById(@NonNull Long id);
}
