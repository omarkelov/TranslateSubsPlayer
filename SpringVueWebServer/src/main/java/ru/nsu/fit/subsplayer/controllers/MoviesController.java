package ru.nsu.fit.subsplayer.controllers;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.services.MovieService;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class MoviesController {

    @Autowired private MovieService movieService;

    @GetMapping(Mappings.MOVIES)
    public String getMovies(@AuthenticationPrincipal UserDetails userDetails) {
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(movieService.queryMovies(userDetails));
    }
}
