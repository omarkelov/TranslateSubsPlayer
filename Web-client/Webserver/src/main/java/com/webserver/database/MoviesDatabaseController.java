package com.webserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MoviesDatabaseController {
  Connection co;

  public MoviesDatabaseController() {
    try {
      Class.forName("org.sqlite.JDBC");
      co = DriverManager.getConnection("jdbc:sqlite:movies.db");
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void insert(String movieName) {
    String query = "INSERT INTO movies (movie_name) " + "VALUES ('" + movieName + "')";
    try {
      Statement statement = co.createStatement();
      statement.executeUpdate(query);
      statement.close();
      System.out.println("Inserted");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void close() {
    try {
      co.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void delete(String movieName) {
    Statement stmt;
    try {
      stmt = co.createStatement();
      String query = "DELETE FROM movies WHERE movie_name='" + movieName + "'";
      stmt.executeUpdate(query);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  String selectMovie(String movieName) {
    try {
      Statement st = co.createStatement();
      String query = "SELECT movie_name FROM movies " + "WHERE movie_name='" + movieName + "'";
      ResultSet rs = st.executeQuery(query);
      String movie = rs.getString("movie_name");
      rs.close();
      st.close();
      return movie;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

}
