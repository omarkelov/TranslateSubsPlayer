package ru.nsu.fit.subsplayer.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AccessoryService {
    void checkPhraseAccess(UserDetails userDetails, long phraseId);
}
