package ru.nsu.fit.subsplayer.database.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.database.entities.RawPhrase;

import java.util.List;

public interface RawPhraseRepository extends CrudRepository<RawPhrase, Long> {

    List<RawPhrase> findByRawMovieId(long rawMovieId);

    void deleteByRawMovieId(long rawMovieId);
}
