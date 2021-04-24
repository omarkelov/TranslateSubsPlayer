package ru.nsu.fit.subsplayer.database.entities;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "phrase_stats")
@Getter @Setter @NoArgsConstructor
public class PhraseStats {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long phraseId;

    private int priority;

    @Expose
    private int successfulAttempts;

    @Expose
    private int attempts;

    public PhraseStats(Long phraseId) {
        this.phraseId = phraseId;
    }

    public void update(boolean correct) {
        attempts++;

        if (correct) {
            priority /= 2;
            successfulAttempts++;
        } else {
            priority++;
        }
    }
}
