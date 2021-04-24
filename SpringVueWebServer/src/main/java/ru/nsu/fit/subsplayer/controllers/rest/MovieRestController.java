package ru.nsu.fit.subsplayer.controllers.rest;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.Movie;
import ru.nsu.fit.subsplayer.database.repositories.MovieRepository;
import ru.nsu.fit.subsplayer.services.ContextService;
import ru.nsu.fit.subsplayer.services.MovieService;

import javax.transaction.Transactional;

@Component
@RestController
@RequestMapping(value = "/", produces = "application/json")
public class MovieRestController {

    @Autowired private MovieService movieService;
    @Autowired private ContextService contextService;

    @Autowired private MovieRepository movieRepository;

    @GetMapping(Mappings.MOVIES)
    public String getMovies(@AuthenticationPrincipal UserDetails userDetails) {
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(movieService.queryMovies(userDetails));
    }

    @GetMapping(Mappings.MOVIES + "/{movieName}")
    public String getMovie(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable String movieName) {

        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(movieService.queryMovie(userDetails, movieName));
    }

    @DeleteMapping(Mappings.MOVIES + "/{movieName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteMovie(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable String movieName) {

        Movie movie = movieService.queryMovie(userDetails, movieName);

        movieRepository.deleteByUserIdAndName(movie.getUserId(), movie.getName());
        for (Context context : movie.getContexts()) {
            contextService.deleteContext(context);
        }
    }
}
