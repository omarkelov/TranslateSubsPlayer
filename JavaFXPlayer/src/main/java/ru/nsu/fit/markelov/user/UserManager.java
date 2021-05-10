package ru.nsu.fit.markelov.user;

import com.google.gson.Gson;
import ru.nsu.fit.markelov.user.entities.Group;
import ru.nsu.fit.markelov.user.entities.Phrase;
import ru.nsu.fit.markelov.subtitles.RawMovie;
import ru.nsu.fit.markelov.user.entities.RawPhraseJson;
import ru.nsu.fit.markelov.user.entities.Translation;
import ru.nsu.fit.markelov.translation.entities.TranslationGroup;
import ru.nsu.fit.markelov.translation.entities.TranslationResult;
import ru.nsu.fit.markelov.translation.entities.TranslationVariant;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UserManager {

    private static final String SITE_URI = "http://127.0.0.1:8081";

    private static final String LOGIN_MAPPING = "/login";
    private static final String LOGOUT_MAPPING = "/logout";
    private static final String PING_MAPPING = "/ping";
    private static final String RAW_MOVIE = "/raw-movie";
    private static final String RAW_MOVIES = "/raw-movies";
    private static final String RAW_PHRASE = "/raw-phrase";

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

    public Boolean checkRawMovieExistence(String hashSum) {
        if (credentials.isEmpty()) {
            System.err.println("Credentials are empty");
            return null;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + RAW_MOVIE + "?hashSum=" + hashSum))
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

            if (response.statusCode() == 204) {
                return true;
            }

            if (response.statusCode() == 412) {
                return false;
            }

            System.out.println("Bad status code: " + response.statusCode());
            return null;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createRawMovie(String hashSum, String videoFilePath, String linesJson) {
        if (credentials.isEmpty()) {
            System.err.println("Credentials are empty");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + RAW_MOVIES))
                .POST(HttpRequest.BodyPublishers.ofString(
                    new Gson().toJson(new RawMovie(hashSum, videoFilePath, linesJson))))
                .header("Cookie", credentials.getSessionCookie())
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response == null) {
                System.err.println("Response is null");
                return;
            }

            if (response.statusCode() != 204) {
                System.out.println("Bad status code: " + response.statusCode());
                return;
            }

            System.out.println("Subtitles successfully sent");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createRawPhrase(String hashSum, int lineId, String phraseText,
                                TranslationResult translationResult)
    {
        if (credentials.isEmpty()) {
            System.err.println("Credentials are empty");
            return;
        }

        List<Group> groups = new ArrayList<>();
        if (translationResult.getTranslationGroups() != null) {
            for (TranslationGroup translationGroup : translationResult.getTranslationGroups()) {
                groups.add(new Group(translationGroup.getPartOfSpeech(), translationGroup.getVariants()
                    .stream().map(TranslationVariant::getWord).collect(Collectors.toList())));
            }
        }
        Translation translation = new Translation(translationResult.getTranslation(), groups);
        Phrase phrase = new Phrase(lineId, phraseText, translation);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SITE_URI + RAW_PHRASE + "?hashSum=" + hashSum))
                .POST(HttpRequest.BodyPublishers.ofString(
                    new Gson().toJson(new RawPhraseJson(new Gson().toJson(phrase)))))
                .header("Cookie", credentials.getSessionCookie())
                .timeout(Duration.of(15, SECONDS))
                .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response == null) {
                System.err.println("Response is null");
                return;
            }

            if (response.statusCode() != 204) {
                System.out.println("Bad status code: " + response.statusCode());
                return;
            }

            System.out.println("Phrase successfully sent");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
