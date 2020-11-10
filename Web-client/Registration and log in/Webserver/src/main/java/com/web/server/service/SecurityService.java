package com.web.server.service;

public interface SecurityService {
  String findLoggedInUsername();

  void autoLogin(String username, String password);
}
