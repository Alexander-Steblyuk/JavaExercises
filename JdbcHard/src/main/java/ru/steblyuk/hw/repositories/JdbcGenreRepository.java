package ru.steblyuk.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private static final String IDS_PARAM_NAME = "ids";
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";

    private final NamedParameterJdbcOperations namedJdbcOperations;

    @Override
    public List<Genre> findAll() {
        return namedJdbcOperations.query("select id, name from genres", new GenreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        SqlParameterSource parameterSource = new MapSqlParameterSource(IDS_PARAM_NAME, ids);
        return namedJdbcOperations.query("select id, name from genres where id in :ids", parameterSource, new GenreRowMapper());
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong(ID_COLUMN_NAME);
            String name = rs.getString(NAME_COLUMN_NAME);
            return new Genre(id, name);
        }
    }
}
