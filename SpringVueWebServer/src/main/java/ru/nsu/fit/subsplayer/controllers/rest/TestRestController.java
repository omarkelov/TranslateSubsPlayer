package ru.nsu.fit.subsplayer.controllers.rest;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.Movie;
import ru.nsu.fit.subsplayer.database.entities.Phrase;
import ru.nsu.fit.subsplayer.database.entities.Test;
import ru.nsu.fit.subsplayer.database.repositories.PhraseStatsRepository;
import ru.nsu.fit.subsplayer.services.MovieService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class TestRestController {

    @Autowired private MovieService movieService;

    @Autowired private PhraseStatsRepository phraseStatsRepository;

    @GetMapping(Mappings.MOVIES + "/{movieName}/test")
    public String getTest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String movieName) {
        Movie movie = movieService.queryMovie(userDetails, movieName);

        List<Phrase> phrases = new ArrayList<>();
        for (Context context : movie.getContexts()) {
            for (Phrase phrase : context.getPhrases()) {
                phrase.setPhraseStats(phraseStatsRepository.findByPhraseId(phrase.getId()));
                phrases.add(phrase);
            }
        }

        phrases = shuffleParts(phrases);

        return new Gson().toJson(new Test(movie.getName(),
            phrases.stream().map(Phrase::getId).collect(Collectors.toList())));
    }

    private List<Phrase> shuffleParts(List<Phrase> phrases) { // todo move to util?
        phrases.sort(Comparator.comparingInt(phrase -> ((Phrase) phrase).getPhraseStats().getPriority()).reversed());

        List<Phrase> shuffledPhrases = new ArrayList<>();
        List<Phrase> currentPhrases = new ArrayList<>();
        Integer currentPriority = null;
        for (Phrase phrase : phrases) {
            if (currentPriority == null || currentPriority != phrase.getPhraseStats().getPriority()) {
                Collections.shuffle(currentPhrases);
                shuffledPhrases.addAll(currentPhrases);

                currentPriority = phrase.getPhraseStats().getPriority();
                currentPhrases.clear();
            }

            currentPhrases.add(phrase);
        }

        Collections.shuffle(currentPhrases);
        shuffledPhrases.addAll(currentPhrases);

        return shuffledPhrases;
    }
}
