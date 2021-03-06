package ru.nsu.fit.subsplayer.entities;

public class MovieContext {

    private Context context;
    private String phrasesJson;

    public MovieContext() {}

    public MovieContext(Context context, String phrasesJson) {
        this.context = context;
        this.phrasesJson = phrasesJson;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPhrasesJson() {
        return phrasesJson;
    }

    public void setPhrasesJson(String phrasesJson) {
        this.phrasesJson = phrasesJson;
    }
}
