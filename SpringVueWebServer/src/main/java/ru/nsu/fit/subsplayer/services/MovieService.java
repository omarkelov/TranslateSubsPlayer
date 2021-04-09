package ru.nsu.fit.subsplayer.services;

import org.springframework.security.core.userdetails.UserDetails;
import ru.nsu.fit.subsplayer.entities.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> queryMovies(UserDetails userDetails);

    Movie queryMovie(UserDetails userDetails, String movieName);
}
