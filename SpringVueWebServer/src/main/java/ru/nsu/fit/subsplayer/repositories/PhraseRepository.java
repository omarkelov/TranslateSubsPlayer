package ru.nsu.fit.subsplayer.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.entities.Phrase;

import java.util.List;

public interface PhraseRepository extends CrudRepository<Phrase, Long> {

    List<Phrase> findByContextId(long contextId);

    List<Phrase> deleteByContextId(long contextId);
}
