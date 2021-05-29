package ru.nsu.fit.subsplayer.database.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.nsu.fit.subsplayer.database.entities.Movie;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    List<Movie> findByUserId(long userId);

    List<Movie> findByUserIdAndName(long userId, String name);

    @Query("""
            SELECT m
            FROM Movie m
            INNER JOIN Context c
                ON m.id = c.movieId
            WHERE c.id = :contextId
        """)
    Movie findByContextId(@Param("contextId") Long contextId);

    void deleteByUserIdAndName(long userId, String name);
}
