package ru.nsu.fit.subsplayer.controllers;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.entities.RawMovie;
import ru.nsu.fit.subsplayer.repositories.RawMovieRepository;
import ru.nsu.fit.subsplayer.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class RawMoviesController {

    @Autowired private UserRepository userRepository;
    @Autowired private RawMovieRepository rawMovieRepository;

    @GetMapping(Mappings.RAW_MOVIES)
    public String getMovies(@AuthenticationPrincipal UserDetails userDetails) {
        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();

        List<RawMovie> rawMovies = rawMovieRepository.findByUserId(userId)
            .stream().map(RawMovie::new).collect(Collectors.toList());

        return new Gson().toJson(rawMovies);
    }
}
