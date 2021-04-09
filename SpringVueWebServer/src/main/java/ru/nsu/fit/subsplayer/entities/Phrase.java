package ru.nsu.fit.subsplayer.entities;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "phrases")
@Getter @Setter @NoArgsConstructor
public class Phrase {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long contextId;

    @Expose
    private String phrase;

    @Expose
    private String correctedPhrase;

    @Expose
    private String type;

    @Expose
    private String translation;

    @Expose
    @Transient
    private PhraseStats phraseStats;

    public Phrase(String phrase, String correctedPhrase, String type, String translation) {
        this.phrase = phrase;
        this.correctedPhrase = correctedPhrase;
        this.type = type;
        this.translation = translation;
    }
}
