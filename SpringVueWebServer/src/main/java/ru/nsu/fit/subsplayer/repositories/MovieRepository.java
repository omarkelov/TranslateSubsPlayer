package ru.nsu.fit.subsplayer.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.entities.Movie;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    List<Movie> findByUserId(long userId);

    List<Movie> findByUserIdAndName(long userId, String name);

    void deleteByUserIdAndName(long userId, String name);
}
