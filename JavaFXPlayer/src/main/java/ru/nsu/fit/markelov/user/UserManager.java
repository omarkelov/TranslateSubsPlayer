package ru.nsu.fit.markelov.user;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UserManager {

    private static final String SITE_URI = "http://127.0.0.1:8081";

    private static final String LOGIN_MAPPING = "/login";
    private static final String LOGOUT_MAPPING = "/logout";
    private static final String PING_MAPPING = "/ping";

    private static final String SESSION_COOKIE_KEY = "JSESSIONID=";

    private final Credentials credentials = new Credentials();

    public String getUsername() {
        return credentials.getUsername();
    }

    public Boolean login(String username, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + LOGIN_MAPPING))
                .POST(HttpRequest.BodyPublishers.ofString("username=" + username + "&password=" + password))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response == null) {
                System.err.println("Response is null");
                return null;
            }

            if (response.statusCode() >= 400) {
                System.out.println("Bad status code: " + response.statusCode());
                return false;
            }

            Optional<String> setCookieHeader = response.headers().firstValue("set-cookie");
            if (setCookieHeader.isEmpty()) {
                System.err.println("set-cookie not present");
                return null;
            }

            for (String cookie : setCookieHeader.get().split("\s*;\s*")) {
                if (cookie.startsWith(SESSION_COOKIE_KEY)) {
                    credentials.updateCredentials(username, cookie);
                    return true;
                }
            }

            System.err.println("No SESSION_COOKIE_KEY among cookies");
            return null;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void logout() {
        if (credentials.isEmpty()) {
            System.err.println("Credentials are empty");
            return;
        }

        String sessionCookie = credentials.getSessionCookie();
        credentials.clearCredentials();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + LOGOUT_MAPPING))
                .GET()
                .header("Cookie", sessionCookie)
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Boolean ping() {
        if (credentials.isEmpty()) {
            System.err.println("Credentials are empty");
            return null;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + PING_MAPPING))
                .GET()
                .header("Cookie", credentials.getSessionCookie())
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response == null) {
                System.err.println("Response is null");
                return null;
            }

            if (response.statusCode() != 204) {
                System.out.println("Bad status code: " + response.statusCode());
                return false;
            }

            return true;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
