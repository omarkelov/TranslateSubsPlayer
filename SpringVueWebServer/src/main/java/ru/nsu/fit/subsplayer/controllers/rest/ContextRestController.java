package ru.nsu.fit.subsplayer.controllers.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.services.AccessoryService;
import ru.nsu.fit.subsplayer.services.ContextService;
import ru.nsu.fit.subsplayer.services.MovieService;

import javax.transaction.Transactional;
import java.io.InvalidObjectException;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class ContextRestController {

    @Autowired private AccessoryService accessoryService;
    @Autowired private MovieService movieService;
    @Autowired private ContextService contextService;

    @Autowired private ContextRepository contextRepository;

    @GetMapping(Mappings.CONTEXT)
    public String getContextByPhraseId(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam long phraseId) {

        accessoryService.checkPhraseAccess(userDetails, phraseId);

        Context context = contextRepository.findByPhraseId(phraseId);
        context.setPhrases(contextService.queryPhrases(context.getId()));

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(context);
    }

    @GetMapping(Mappings.CONTEXTS + "/{contextId}")
    public String getContext(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable long contextId) {

        accessoryService.checkContextAccess(userDetails, contextId);

        Context context = contextRepository.findById(contextId).get();
        context.setPhrases(contextService.queryPhrases(context.getId()));

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(context);
    }

    @DeleteMapping(Mappings.CONTEXTS + "/{contextId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteContext(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable long contextId) {

        accessoryService.checkContextAccess(userDetails, contextId);

        Context context = contextRepository.findById(contextId).get();
        contextService.deleteContext(context);
    }

    @PostMapping(Mappings.CONTEXTS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createContext(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam String movieName,
                              @RequestBody String body) {

        Context context;
        try {
            context = new Gson().fromJson(body, Context.class);
            context.validate();
        } catch (JsonSyntaxException | InvalidObjectException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        long movieId = movieService.queryMovie(userDetails, movieName).getId();
        context.setMovieId(movieId);

        contextService.saveContext(context);
    }
}
