package ru.nsu.fit.subsplayer.services;

import ru.nsu.fit.subsplayer.entities.Context;
import ru.nsu.fit.subsplayer.entities.Phrase;

import java.util.List;

public interface ContextService {

    List<Phrase> queryPhrases(long contextId);

    void deleteContext(Context context);
}
