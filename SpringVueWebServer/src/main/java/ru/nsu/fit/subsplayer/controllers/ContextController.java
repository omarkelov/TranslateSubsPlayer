package ru.nsu.fit.subsplayer.controllers;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.entities.Context;
import ru.nsu.fit.subsplayer.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.services.AccessoryService;
import ru.nsu.fit.subsplayer.services.ContextService;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class ContextController {

    @Autowired private AccessoryService accessoryService;
    @Autowired private ContextService contextService;

    @Autowired private ContextRepository contextRepository;

    @GetMapping(Mappings.CONTEXT)
    public String getContext(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam long phraseId) {

        accessoryService.checkPhraseAccess(userDetails, phraseId);

        Context context = contextRepository.findByPhraseId(phraseId);
        context.setPhrases(contextService.queryPhrases(context.getId()));

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(context);
    }
}
