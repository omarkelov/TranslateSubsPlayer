package ru.nsu.fit.subsplayer.controllers;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.nsu.fit.subsplayer.entities.Context;
import ru.nsu.fit.subsplayer.entities.MovieContext;
import ru.nsu.fit.subsplayer.entities.Phrase;
import ru.nsu.fit.subsplayer.repo.ContextRepository;
import ru.nsu.fit.subsplayer.repo.PhraseRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private ContextRepository contextRepository;
    @Autowired
    private PhraseRepository phraseRepository;

    @GetMapping("/")
    public String main(Model model) {
        List<MovieContext> movieContexts = new ArrayList<>();
        for (Context context : contextRepository.findAll()) {
            List<Phrase> phrases = phraseRepository.findByContextId(context.getId());
            movieContexts.add(new MovieContext(context, new Gson().toJson(phrases)));
        }

        model.addAttribute("movieName", "Rêver Chiot");
        model.addAttribute("movieContexts", movieContexts);

        return "main";
    }

    @GetMapping("/add")
    public String add(Model model) {
        contextRepository.save(new Context(2L, "Lorem superposés valise pourparlers rêver chiots rendez-vous naissance Eiffel myrtille.", "videos/1.mp4"));
        contextRepository.save(new Context(2L, "Nous avoir parole la nous moussant.", "videos/2.mp4"));
        contextRepository.save(new Context(2L, "Bourguignon penser câlin millésime peripherique annoncer enfants enfants vachement nuit formidable encombré épanoui chiots.", "videos/3.mp4"));

        phraseRepository.save(new Phrase(1L, "rêver chiots", "rêver chiot", "phrase", "dreaming puppy"));
        phraseRepository.save(new Phrase(2L, "parole la nous", "NULL", "phraseme", "talk to us"));
        phraseRepository.save(new Phrase(3L, "penser", "NULL", "word", "think"));
        phraseRepository.save(new Phrase(3L, "nuit", "NULL", "word", "night"));

        return "main";
    }
}
