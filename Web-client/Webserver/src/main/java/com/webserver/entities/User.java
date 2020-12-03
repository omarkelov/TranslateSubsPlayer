package com.webserver.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.util.Assert;

/**
 * A class to store application user data.
 *
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * The auto-generated id of a user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(nullable = false)
  private Integer id;
  /**
   * A username to login to the application.
   */
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(nullable = false, length = 255, unique = true)
  private String username;
  /**
   * A password to login to the application.
   */
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(nullable = false, length = 255)
  private String password;
  /**
   * Movie list of a user.
   */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Movie> movies = new HashSet<>();

  /**
   * A no-argument constructor.
   */
  public User() {}

  /**
   * A constructor used to create users.
   *
   * @param username
   * @param password
   */
  public User(String username, String password) {
    Assert.hasLength(username);
    Assert.hasLength(password);
    this.username = username;
    this.password = password;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    Assert.hasLength(username);
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    Assert.hasLength(password);
    this.password = password;
  }

  public Set<Movie> getMovies() {
    return movies;
  }

  /**
   * A method to add movies to user's list.
   *
   * @param movie a movie to add.
   * @return the added movie.
   */
  public Movie addMovie(Movie movie) {
    Assert.notNull(movie);
    movies.add(movie);
    movie.setUser(this);
    return movie;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof User)) {
      return false;
    }
    User other = (User) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "User{" + "id=" + id + ", username=" + username + ", password=" + password + '}';
  }

}
