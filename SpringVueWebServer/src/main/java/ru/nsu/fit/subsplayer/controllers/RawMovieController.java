package ru.nsu.fit.subsplayer.controllers;

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
import ru.nsu.fit.subsplayer.entities.RawMovie;
import ru.nsu.fit.subsplayer.repositories.RawMovieRepository;
import ru.nsu.fit.subsplayer.repositories.RawPhraseRepository;
import ru.nsu.fit.subsplayer.repositories.UserRepository;
import ru.nsu.fit.subsplayer.services.AccessoryService;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class RawMovieController {

    @Autowired private AccessoryService accessoryService;

    @Autowired private UserRepository userRepository;
    @Autowired private RawMovieRepository rawMovieRepository;
    @Autowired private RawPhraseRepository rawPhraseRepository;

    @GetMapping(Mappings.RAW_MOVIES + "/{rawMovieId}")
    public String getRawMovie(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable Long rawMovieId) {

        accessoryService.checkRawMovieAccess(userDetails, rawMovieId);

        RawMovie rawMovie = rawMovieRepository.findById(rawMovieId).get();
        rawMovie.setPhrases(rawPhraseRepository.findByRawMovieId(rawMovie.getId()));

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(rawMovie);
    }

    @DeleteMapping(Mappings.RAW_MOVIES + "/{rawMovieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteRawMovie(@AuthenticationPrincipal UserDetails userDetails,
                               @PathVariable Long rawMovieId) {

        accessoryService.checkRawMovieAccess(userDetails, rawMovieId);

        rawMovieRepository.deleteById(rawMovieId);
        rawPhraseRepository.deleteByRawMovieId(rawMovieId);
    }

    @PostMapping(Mappings.RAW_MOVIES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createRawMovie(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestBody String body) {

        RawMovie rawMovie;
        try {
            rawMovie = new Gson().fromJson(body, RawMovie.class);
        } catch (JsonSyntaxException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        if (rawMovie.getHashSum() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "'hashSum' parameter is not present");
        }
        if (rawMovie.getVideoFilePath() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "'videoFilePath' parameter is not present");
        }
        if (rawMovie.getLines() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "'lines' parameter is not present");
        }

        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        if (rawMovieRepository.existsByUserIdAndHashSum(userId, rawMovie.getHashSum())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Subtitles (hash sum " + rawMovie.getHashSum() + ") already exist");
        }

        rawMovie.setUserId(userId);
        rawMovieRepository.save(rawMovie);
    }

    @GetMapping(Mappings.RAW_MOVIE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkRawMovieExistence(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam String hashSum) {

        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        if (!rawMovieRepository.existsByUserIdAndHashSum(userId, hashSum)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Subtitles (hash sum " + hashSum + ") not found");
        }
    }
}
