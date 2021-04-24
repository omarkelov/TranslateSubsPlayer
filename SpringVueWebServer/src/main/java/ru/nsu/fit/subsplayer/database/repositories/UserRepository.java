package ru.nsu.fit.subsplayer.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nsu.fit.subsplayer.database.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query("""
            SELECT u
            FROM User u
            INNER JOIN RawMovie rm
                ON u.id = rm.userId
            WHERE rm.id = :rawMovieId
        """)
    User findByRawMovieId(@Param("rawMovieId") Long rawMovieId);

    @Query("""
            SELECT u
            FROM User u
            INNER JOIN Movie m
                ON u.id = m.userId
            WHERE m.id = :movieId
        """)
    User findByMovieId(@Param("movieId") Long movieId);

    @Query("""
            SELECT u
            FROM User u
            INNER JOIN Movie m
                ON u.id = m.userId
            INNER JOIN Context c
                ON m.id = c.movieId
            WHERE c.id = :contextId
        """)
    User findByContextId(@Param("contextId") Long contextId);

    @Query("""
            SELECT u
            FROM User u
            INNER JOIN Movie m
                ON u.id = m.userId
            INNER JOIN Context c
                ON m.id = c.movieId
            INNER JOIN Phrase p
                ON c.id = p.contextId
            WHERE p.id = :phraseId
        """)
    User findByPhraseId(@Param("phraseId") Long phraseId);
}
