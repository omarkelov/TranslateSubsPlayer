package ru.nsu.fit.subsplayer.controllers.rest;

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
import ru.nsu.fit.subsplayer.database.entities.RawPhrase;
import ru.nsu.fit.subsplayer.database.repositories.RawMovieRepository;
import ru.nsu.fit.subsplayer.database.repositories.RawPhraseRepository;
import ru.nsu.fit.subsplayer.database.repositories.UserRepository;

import java.io.InvalidObjectException;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class RawPhraseRestController {

    @Autowired private UserRepository userRepository;
    @Autowired private RawMovieRepository rawMovieRepository;
    @Autowired private RawPhraseRepository rawPhraseRepository;

    @PostMapping(Mappings.RAW_PHRASE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createRawPhrase(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String hashSum,
                                @RequestBody String body) {

        RawPhrase rawPhrase;
        try {
            rawPhrase = new Gson().fromJson(body, RawPhrase.class);
            rawPhrase.validate();
        } catch (JsonSyntaxException | InvalidObjectException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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
