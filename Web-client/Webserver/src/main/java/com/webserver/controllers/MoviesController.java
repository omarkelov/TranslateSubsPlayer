package com.webserver.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webserver.entities.Movie;
import com.webserver.entities.User;
import com.webserver.exception.MovieNotFoundException;
import com.webserver.exception.UserNotFoundException;
import com.webserver.repositories.MoviesRepository;
import com.webserver.repositories.UsersRepository;

/**
 * The controller that exposes resource methods to work with movies for a particular user.
 */
@RequestMapping("/{username}/movies")
@RestController
public class MoviesController {

  /**
   * The repository to work with movies.
   */
  private final MoviesRepository moviesRepository;
  /**
   * The repository to work with users.
   */
  private final UsersRepository usersRepository;

  /**
   * The constructor which allows to inject repositories.
   *
   * @param moviesRepository The repository to work with movies.
   * @param usersRepository The repository to work with users.
   */
  @Autowired
  public MoviesController(MoviesRepository moviesRepository, UsersRepository usersRepository) {
    this.moviesRepository = moviesRepository;
    this.usersRepository = usersRepository;
  }

  /**
   * A method to return movies for a particular user.
   *
   * @param username the name of a user whose movies are listed.
   * @return list of user's movies.
   * @throws java.lang.Exception
   */
  @RequestMapping(method = RequestMethod.GET)
  public Set<Movie> getAllMovies(@PathVariable(value = "username") String username)
      throws Exception {
    validateUser(username);
    return moviesRepository.findByUserUsername(username);
  }

  /**
   * A method to find a movie by id.
   *
   * @param movieId
   */
  @RequestMapping(value = "/{movieId}", method = RequestMethod.GET)
  public Movie getMovie(@PathVariable(value = "username") String username,
      @PathVariable(value = "movieId") Integer movieId)
      throws UserNotFoundException, MovieNotFoundException {
    validateUser(username);
    Optional<Movie> optional = moviesRepository.findByIdAndUserUsername(movieId, username);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new MovieNotFoundException(movieId.toString());
    }
  }

  /**
   * A method to add a movie.
   */
  @RequestMapping(method = RequestMethod.POST)
  ResponseEntity<Movie> addMovie(@PathVariable(value = "username") String username,
      @RequestBody Movie movie) throws UserNotFoundException {
    Optional<User> optional = usersRepository.findByUsername(username);
    if (optional.isPresent()) {
      User user = optional.get();
      user.addMovie(movie);
      movie.setUser(user);
      moviesRepository.save(movie);
      return new ResponseEntity<>(movie, HttpStatus.CREATED);
    } else {
      throw new UserNotFoundException(username);
    }
  }

  /**
   * A method to edit a movie.
   *
   * @param username
   * @param movieId
   * @param json
   * @return ResponseEntity containing the patched movie, if found, and status code.
   * @throws java.io.IOException
   * @throws java.lang.reflect.InvocationTargetException
   * @throws com.webserver.exception.MovieNotFoundException
   * @throws java.lang.IllegalAccessException
   */
  @RequestMapping(value = "/{movieId}", method = RequestMethod.PUT)
  public ResponseEntity<Movie> editMovie(@PathVariable(value = "username") String username,
      @PathVariable(value = "movieId") int movieId, @RequestBody String json) throws IOException,
      MovieNotFoundException, IllegalAccessException, InvocationTargetException {

    Optional<Movie> optional = moviesRepository.findByIdAndUserUsername(movieId, username);
    if (optional.isPresent()) {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, String> changeMap = mapper.readValue(json, HashMap.class);
      Movie movie = optional.get();
      BeanUtils.populate(movie, changeMap);
      movie = moviesRepository.save(movie);
      return new ResponseEntity<>(movie, HttpStatus.OK);
    } else {
      throw new MovieNotFoundException("Movie not found id = " + movieId);
    }

  }

  /**
   * A method to delete a movie identified by id.
   *
   * @param username user name
   * @param movieId The id of the movie to be deleted.
   * @return ResponseEntity containing a deleted movie, if found, and status code.
   */
  @RequestMapping(value = "/{movieId}", method = RequestMethod.DELETE)
  public ResponseEntity<Movie> deleteMovie(@PathVariable(value = "username") String username,
      @PathVariable(value = "MovieId") int movieId) throws MovieNotFoundException {
    Optional<Movie> optional = moviesRepository.findByIdAndUserUsername(movieId, username);
    if (optional.isPresent()) {
      moviesRepository.delete(optional.get());
      return new ResponseEntity<>(optional.get(), HttpStatus.OK);
    } else {
      throw new MovieNotFoundException("Movie not found. id = " + movieId);
    }
  }

  /**
   * A method to check if a user exists.
   *
   * @param username username.
   * @throws UserNotFoundException thrown if user doesn't exist.
   */
  private void validateUser(String username) throws UserNotFoundException {
    if (!usersRepository.findByUsername(username).isPresent()) {
      throw new UserNotFoundException(username);
    }
  }

}
