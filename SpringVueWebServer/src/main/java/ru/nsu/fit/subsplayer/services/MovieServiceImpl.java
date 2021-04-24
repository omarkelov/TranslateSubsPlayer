package ru.nsu.fit.subsplayer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.Movie;
import ru.nsu.fit.subsplayer.database.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.database.repositories.MovieRepository;
import ru.nsu.fit.subsplayer.database.repositories.UserRepository;

import java.util.List;

@Component
public class MovieServiceImpl implements MovieService {

    @Autowired private ContextService contextService;

    @Autowired private UserRepository userRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ContextRepository contextRepository;

    @Override
    public List<Movie> queryMovies(UserDetails userDetails) {
        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();

        List<Movie> movies = movieRepository.findByUserId(userId);
        for (Movie movie : movies) {
            movie.setHref(Mappings.MOVIES + "/" + movie.getName());
        }

        return movies;
    }

    public Movie queryMovie(UserDetails userDetails, String movieName) {
        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        List<Movie> movies = movieRepository.findByUserIdAndName(userId, movieName);

        if (movies.size() < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, movieName + " not found");
        }

        if (movies.size() > 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Multiple '" + movieName + "' records found");
        }

        Movie movie = movies.get(0);
        movie.setContexts(contextRepository.findByMovieId(movie.getId()));
        for (Context context : movie.getContexts()) {
            context.setPhrases(contextService.queryPhrases(context.getId()));
        }

        return movie;
    }
}
