package ru.nsu.fit.subsplayer.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.entities.RawMovie;

import java.util.List;

public interface RawMovieRepository extends CrudRepository<RawMovie, Long> {

    boolean existsByUserIdAndHashSum(long userId, String hashSum);

    RawMovie.OnlyId findByUserIdAndHashSum(long userId, String hashSum);

    List<RawMovie.OnlyIdAndVideoFileName> findByUserId(long userId);
}
