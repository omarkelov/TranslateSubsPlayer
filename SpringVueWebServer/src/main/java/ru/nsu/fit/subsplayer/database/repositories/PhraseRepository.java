package ru.nsu.fit.subsplayer.database.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.database.entities.Phrase;

import java.util.List;

public interface PhraseRepository extends CrudRepository<Phrase, Long> {

    List<Phrase> findByContextId(long contextId);

    List<Phrase> deleteByContextId(long contextId);
}
