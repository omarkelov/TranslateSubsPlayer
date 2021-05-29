package ru.nsu.fit.subsplayer.database.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.nsu.fit.subsplayer.database.entities.Context;

import java.util.List;

public interface ContextRepository extends CrudRepository<Context, Long> {

    List<Context> findByMovieId(long movieId);

    void deleteByMovieId(long movieId);

    @Query("""
            SELECT c
            FROM Context c
            INNER JOIN Phrase p
                ON c.id = p.contextId
            WHERE p.id = :phraseId
        """)
    Context findByPhraseId(@Param("phraseId") Long phraseId);

    @Query("""
            SELECT c
            FROM Context c
            INNER JOIN Movie m
                ON c.movieId = m.id
            INNER JOIN User u
                ON m.userId = u.id
            WHERE u.id = :userId AND c.link IS NULL
        """)
    List<Context> findWithNoLink(@Param("userId") long userId);
}
