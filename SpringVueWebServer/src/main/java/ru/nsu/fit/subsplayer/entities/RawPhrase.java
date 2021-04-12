package ru.nsu.fit.subsplayer.entities;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "raw_phrases")
@Getter @Setter @NoArgsConstructor
public class RawPhrase {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long rawMovieId;

    @Expose
    @Column(length = 8 * 1024)
    private String phraseJson;

    @Expose
    private Boolean handled;

    public RawPhrase(String phraseJson, Boolean handled) {
        this.phraseJson = phraseJson;
        this.handled = handled;
    }
}
