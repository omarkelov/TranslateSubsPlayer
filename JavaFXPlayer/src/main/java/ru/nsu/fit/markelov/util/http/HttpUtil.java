package ru.nsu.fit.markelov.util.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class HttpUtil {

    private static final String USER_AGENT_KEY = "user-agent";
    private static final String USER_AGENT_VALUE =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0";

    public static String getContent(String url) throws URISyntaxException, IOException, InterruptedException { // todo HttpTimeoutException, SecurityException
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .GET()
            .header(USER_AGENT_KEY, USER_AGENT_VALUE)
            .timeout(Duration.of(15, SECONDS))
            .build();

        HttpResponse<String> response = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString());

        if (response == null || response.body() == null) {
            throw new IOException("Response is null"); // todo change exception
        }

        return response.body();
    }
}
