package ru.nsu.fit.subsplayer.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.fit.subsplayer.entities.PhraseStats;

public interface PhraseStatsRepository extends CrudRepository<PhraseStats, Long> {

    PhraseStats findByPhraseId(long phraseId);

    void deleteByPhraseId(long phraseId);
}
