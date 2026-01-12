package ru.steblyuk.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {
    private static final String ID_PARAM_NAME = "id";
    private static final String ID_COLUMN_NAME = "id";
    private static final String FULL_NAME_COLUMN_NAME = "full_name";

    private final NamedParameterJdbcOperations namedJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedJdbcOperations.query("select id, full_name from authors", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        SqlParameterSource parameterSource = new MapSqlParameterSource(ID_PARAM_NAME, id);
        return namedJdbcOperations.query("select id, full_name from authors where id = :id", parameterSource, new AuthorRowMapper()).stream()
                .findFirst();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong(ID_COLUMN_NAME);
            String fullName = rs.getString(FULL_NAME_COLUMN_NAME);
            return new Author(id, fullName);
        }
    }
}
