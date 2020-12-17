package com.webserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ContextsDatabaseController {
  Connection co;

  public ContextsDatabaseController() {
    try {
      Class.forName("org.sqlite.JDBC");
      co = DriverManager.getConnection("jdbc:sqlite:contexts.db");
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void insert(int movieId, String context, String link) {
    String query = "INSERT INTO contexts (movieId, context, link) " + "VALUES (" + movieId + ", '"
        + context + ", '" + link + "')";
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

  public void delete(String name) {
    Statement stmt;
    try {
      stmt = co.createStatement();
      String query = "DELETE FROM context WHERE name='" + name + "'"; // по какому параметру удалять
                                                                      // контекст?
      stmt.executeUpdate(query);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /*
   * аналогично, по какому параметру из базы данных брать контекст Pair<String, String>
   * selectPerson(String name) { try { Statement st = co.createStatement(); String query =
   * "SELECT id, name, password FROM users " + "WHERE name='" + name + "'"; ResultSet rs =
   * st.executeQuery(query); Pair<String, String> nameAndPass = new Pair<String,
   * String>(rs.getString("name"), rs.getString("password")); rs.close(); st.close(); return
   * nameAndPass; } catch (SQLException e) { System.out.println(e.getMessage()); } return null; }
   */

}

