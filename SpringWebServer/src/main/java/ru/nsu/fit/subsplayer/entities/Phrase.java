package ru.nsu.fit.subsplayer.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Phrase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long contextId;
    private String phrase;
    private String correctedPhrase;
    private String type;
    private String translation;
    private int priority;
    private int successfulAttempts;
    private int attempts;

    public Phrase() {}

    public Phrase(Long contextId, String phrase, String correctedPhrase, String type, String translation) {
        this.contextId = contextId;
        this.phrase = phrase;
        this.correctedPhrase = correctedPhrase;
        this.type = type;
        this.translation = translation;
        this.priority = 0;
        this.successfulAttempts = 0;
        this.attempts = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getCorrectedPhrase() {
        return correctedPhrase;
    }

    public void setCorrectedPhrase(String correctedPhrase) {
        this.correctedPhrase = correctedPhrase;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getSuccessfulAttempts() {
        return successfulAttempts;
    }

    public void setSuccessfulAttempts(int successfulAttempts) {
        this.successfulAttempts = successfulAttempts;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}
