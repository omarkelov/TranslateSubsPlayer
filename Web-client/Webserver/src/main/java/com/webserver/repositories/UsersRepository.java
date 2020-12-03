package com.webserver.repositories;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.webserver.entities.User;

/**
 * Users repository.
 *
 */
public interface UsersRepository extends PagingAndSortingRepository<User, Integer> {

  /**
   * The method looks for a user by username.
   *
   * @param username the name of the user to find.
   * @return Optional of a user, empty if not found.
   */
  Optional<User> findByUsername(String username);

  /**
   * The method looks for a user by username and password for authentication purposes.
   *
   * @param username
   * @param password
   * @return Optional of user if found and empty Optional otherwise.
   */
  Optional<User> findByUsernameAndPassword(String username, String password);
}
