package ru.nsu.fit.subsplayer.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.entities.RawPhrase;
import ru.nsu.fit.subsplayer.repositories.RawMovieRepository;
import ru.nsu.fit.subsplayer.repositories.RawPhraseRepository;
import ru.nsu.fit.subsplayer.repositories.UserRepository;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class RawPhraseController {

    @Autowired private UserRepository userRepository;
    @Autowired private RawMovieRepository rawMovieRepository;
    @Autowired private RawPhraseRepository rawPhraseRepository;

    @PostMapping(Mappings.RAW_PHRASE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createRawMovie(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam String hashSum,
                               @RequestBody String body) {

        RawPhrase rawPhrase;
        try {
            rawPhrase = new Gson().fromJson(body, RawPhrase.class);
        } catch (JsonSyntaxException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (rawPhrase.getPhraseJson() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'phraseJson' parameter is not present");
        }

        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        Long rawMovieId = rawMovieRepository.findByUserIdAndHashSum(userId, hashSum).getId();

        if (rawMovieId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Subtitles (hash sum " + hashSum + ") not found");
        }

        rawPhrase.setRawMovieId(rawMovieId);

        rawPhraseRepository.save(rawPhrase);
    }
}
