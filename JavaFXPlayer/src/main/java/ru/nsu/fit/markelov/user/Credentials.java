package ru.nsu.fit.markelov.user;

import java.util.prefs.Preferences;

public class Credentials {

    private static final String USERNAME_COOKIE_KEY = "username";
    private static final String SESSION_COOKIE_KEY = "sessionCookie";

    private final Preferences preferences;

    private String username;
    private String sessionCookie;

    public Credentials() {
        preferences = Preferences.userRoot().node(getClass().getName());

        initCredentials();
    }

    private void initCredentials() {
        username = preferences.get(USERNAME_COOKIE_KEY, null);
        sessionCookie = preferences.get(SESSION_COOKIE_KEY, null);
    }

    public void updateCredentials(String username, String sessionCookie) {
        this.username = username;
        this.sessionCookie = sessionCookie;

        preferences.put(USERNAME_COOKIE_KEY, username);
        preferences.put(SESSION_COOKIE_KEY, sessionCookie);
    }

    public void clearCredentials() {
        username = null;
        sessionCookie = null;

        preferences.remove(USERNAME_COOKIE_KEY);
        preferences.remove(SESSION_COOKIE_KEY);
    }

    public boolean isEmpty() {
        return username == null;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionCookie() {
        return sessionCookie;
    }
}
