package ru.nsu.fit.subsplayer.database.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.database.entities.PhraseStats;

public interface PhraseStatsRepository extends CrudRepository<PhraseStats, Long> {

    PhraseStats findByPhraseId(long phraseId);

    void deleteByPhraseId(long phraseId);
}
