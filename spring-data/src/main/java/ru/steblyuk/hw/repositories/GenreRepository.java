package ru.steblyuk.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
