package com.webserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsersDatabaseController {
  Connection co;

  public UsersDatabaseController() {
    try {
      Class.forName("org.sqlite.JDBC");
      co = DriverManager.getConnection("jdbc:sqlite:users.db");
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void insert(String name, String password) {
    String query =
        "INSERT INTO users (name, password) " + "VALUES ('" + name + "', '" + password + "')";
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
      String query = "DELETE FROM users WHERE name='" + name + "'";
      stmt.executeUpdate(query);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  Pair<String, String> selectPerson(String name) {
    try {
      Statement st = co.createStatement();
      String query = "SELECT id, name, password FROM users " + "WHERE name='" + name + "'";
      ResultSet rs = st.executeQuery(query);
      Pair<String, String> nameAndPass =
          new Pair<String, String>(rs.getString("name"), rs.getString("password"));
      rs.close();
      st.close();
      return nameAndPass;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

}

