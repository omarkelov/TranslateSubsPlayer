package com.webserver.repositories;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.webserver.entities.Movie;

public interface MoviesRepository extends PagingAndSortingRepository<Movie, Integer> {

  /**
   * A method to find movies stored by a particular user identified by the username.
   *
   * @param username the username.
   * @return list of movies stored by a particular user.
   */
  Set<Movie> findByUserUsername(String username);

  /**
   * A method to find a movie for a particular user with the id specified.
   */
  Optional<Movie> findByIdAndUserUsername(Integer id, String username);
}
