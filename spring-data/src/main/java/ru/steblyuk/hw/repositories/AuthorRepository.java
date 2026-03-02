package ru.steblyuk.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
