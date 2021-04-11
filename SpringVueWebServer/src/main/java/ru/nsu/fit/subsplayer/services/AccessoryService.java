package ru.nsu.fit.subsplayer.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AccessoryService {

    void checkRawMovieAccess(UserDetails userDetails, long rawMovieId);

    void checkContextAccess(UserDetails userDetails, long contextId);

    void checkPhraseAccess(UserDetails userDetails, long phraseId);
}
