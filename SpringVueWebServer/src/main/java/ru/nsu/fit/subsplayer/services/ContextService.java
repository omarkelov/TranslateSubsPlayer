package ru.nsu.fit.subsplayer.services;

import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.Phrase;

import java.util.List;

public interface ContextService {

    List<Phrase> queryPhrases(long contextId);

    void deleteContext(Context context);
}
