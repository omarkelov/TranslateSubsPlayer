package ru.nsu.fit.subsplayer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.Phrase;
import ru.nsu.fit.subsplayer.database.entities.PhraseStats;
import ru.nsu.fit.subsplayer.database.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.database.repositories.PhraseRepository;
import ru.nsu.fit.subsplayer.database.repositories.PhraseStatsRepository;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class ContextServiceImpl implements ContextService {

    @Autowired private ContextRepository contextRepository;
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

    @Override
    @Transactional
    public void deleteContext(Context context) {
        contextRepository.delete(context);
        List<Phrase> phrases = phraseRepository.deleteByContextId(context.getId());
        for (Phrase phrase : phrases) {
            phraseStatsRepository.deleteByPhraseId(phrase.getId());
        }
    }

    @Override
    @Transactional
    public void saveContext(Context context) {
        contextRepository.save(context);
        for (Phrase phrase : context.getPhrases()) {
            phrase.setContextId(context.getId());
            phraseRepository.save(phrase);
            phraseStatsRepository.save(new PhraseStats(phrase.getId()));
        }
    }
}
