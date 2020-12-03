package com.webserver.components;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import com.webserver.entities.User;
import com.webserver.repositories.UsersRepository;

@Component
public class JpaAuthenticationProvider implements AuthenticationProvider {

  /**
   * A user repository.
   */
  @Autowired
  private UsersRepository usersRepository;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Optional<User> optional = usersRepository.findByUsernameAndPassword(authentication.getName(),
        authentication.getCredentials().toString());
    if (optional.isPresent()) {
      return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
          authentication.getCredentials(), authentication.getAuthorities());

    } else {
      throw new AuthenticationCredentialsNotFoundException("Wrong credentials.");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
