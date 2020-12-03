package com.webserver.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import com.webserver.components.JpaAuthenticationProvider;

@Configuration
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {

  /**
   * Custom authentication provider.
   */
  @Autowired
  private JpaAuthenticationProvider authenticationProvider;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authenticationProvider(authenticationProvider).authorizeRequests().anyRequest()
        .authenticated().and().httpBasic().and().cors().and().csrf().disable();
  }

}
