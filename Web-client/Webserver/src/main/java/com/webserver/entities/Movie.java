package com.webserver.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.util.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * The auto-generated id of a movie.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(nullable = false)
  private Integer id;
  /**
   * The URL of a movie.
   */
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(nullable = false, length = 255)
  private String url;

  /**
   * The owner of a movie.
   */
  @JsonIgnore
  @Basic(optional = false)
  @NotNull
  @ManyToOne
  private User user;

  /**
   * The no-argument constructor.
   */
  public Movie() {}

  /**
   * A constructor to create a movie.
   *
   * @param url movie URL.
   */
  public Movie(String url) {
    Assert.hasLength(url);
    this.url = url;
  }

  /**
   * ID getter.
   *
   * @return id
   */
  public Integer getId() {
    return id;
  }

  /**
   * ID setter
   *
   * @param id id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    Assert.hasLength(url);
    this.url = url;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    Assert.notNull(user);
    this.user = user;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Movie)) {
      return false;
    }
    Movie other = (Movie) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Movie{" + "id=" + id + ", url=" + url + ", user=" + user + '}';
  }

}
