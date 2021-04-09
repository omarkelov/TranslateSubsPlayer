package ru.nsu.fit.subsplayer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.entities.PhraseStats;
import ru.nsu.fit.subsplayer.repositories.PhraseStatsRepository;
import ru.nsu.fit.subsplayer.services.AccessoryService;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class PhraseController {

    @Autowired private AccessoryService accessoryService;

    @Autowired private PhraseStatsRepository phraseStatsRepository;

    @PatchMapping(Mappings.PHRASES + "/{phraseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePhrasePriority(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Long phraseId, @RequestParam boolean correct) {

        accessoryService.checkPhraseAccess(userDetails, phraseId);

        PhraseStats phraseStats = phraseStatsRepository.findByPhraseId(phraseId);
        phraseStats.update(correct);
        phraseStatsRepository.save(phraseStats);
    }
}
