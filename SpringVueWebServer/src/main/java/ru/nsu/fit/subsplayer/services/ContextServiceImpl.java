package ru.nsu.fit.subsplayer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.fit.subsplayer.entities.Phrase;
import ru.nsu.fit.subsplayer.repositories.PhraseRepository;
import ru.nsu.fit.subsplayer.repositories.PhraseStatsRepository;

import java.util.List;

@Component
public class ContextServiceImpl implements ContextService {

    @Autowired private PhraseRepository phraseRepository;
    @Autowired private PhraseStatsRepository phraseStatsRepository;

    @Override
    public List<Phrase> queryPhrases(long contextId) {
        List<Phrase> phrases = phraseRepository.findByContextId(contextId);

        for (Phrase phrase : phrases) {
            phrase.setPhraseStats(phraseStatsRepository.findByPhraseId(phrase.getId()));
        }

        return phrases;
    }
}
