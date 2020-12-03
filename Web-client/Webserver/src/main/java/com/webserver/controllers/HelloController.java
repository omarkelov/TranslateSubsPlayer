package com.webserver.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hello")
@RestController
public class HelloController {

  /**
   * A simple greeting.
   */
  public static final String GREETING = "Hello, my dear friend!";

  /**
   * The resource method returns a greeting.
   *
   * @return a greeting.
   */
  // @RequestMapping
  public String getGreeting() {
    return GREETING;
  }

  /**
   * The resource method with a path parameter to return customized greeting.
   *
   * @param name The name of a person to greet.
   * @return Customized greeting.
   */
  @RequestMapping("/{name}")
  public String getPathParamGreeting(@PathVariable String name) {
    return "Hello " + name;
  }

  /**
   * The resource method with a query parameter to return customized greeting.
   *
   * @param name The name of a person to greet.
   * @return Customized greeting.
   */
  @RequestMapping
  public String getQueryParamGreeting(@RequestParam(value = "name", required = false) String name) {
    if (name != null) {
      return "Hello " + name;
    } else {
      return GREETING;
    }
  }
}
