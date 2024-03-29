package ru.nsu.fit.subsplayer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import ru.nsu.fit.subsplayer.constants.Mappings;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .formLogin()
                .loginPage(Mappings.LOGIN)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
            .logout();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder // todo passwordEncoder
            .jdbcAuthentication()
            .dataSource(dataSource)
            .passwordEncoder(NoOpPasswordEncoder.getInstance())
            .usersByUsernameQuery("""
                SELECT username, password, active
                FROM users
                WHERE username=?
            """)
            .authoritiesByUsernameQuery("""
                SELECT u.username, ur.user_roles
                FROM users u
                INNER JOIN user_roles ur
                    ON u.id = ur.user_id
                WHERE u.username=?
            """);
    }
}
