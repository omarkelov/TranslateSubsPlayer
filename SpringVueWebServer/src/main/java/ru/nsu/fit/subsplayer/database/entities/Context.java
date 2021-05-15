package ru.nsu.fit.subsplayer.database.entities;

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
import javax.persistence.Transient;
import java.io.InvalidObjectException;
import java.util.List;

@Entity
@Table(name = "contexts")
@Getter @Setter @NoArgsConstructor
public class Context {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long movieId;

    @Expose
    @Column(length = 4096)
    private String context;

    private Long startTime;

    private Long endTime;

    @Expose
    private String link;

    @Expose
    @Transient
    private List<Phrase> phrases;

    public Context(String context, Long startTime, Long endTime, String link, List<Phrase> phrases) {
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.link = link;
        this.phrases = phrases;
    }

    public void validate() throws InvalidObjectException {
        if (context == null)
            throw new InvalidObjectException("'context' parameter is not present");
        if (startTime == null)
            throw new InvalidObjectException("'startTime' parameter is not present");
        if (endTime == null)
            throw new InvalidObjectException("'endTime' parameter is not present");
        if (phrases == null)
            throw new InvalidObjectException("'phrases' parameter is not present");

        for (Phrase phrase : phrases) {
            phrase.validate();
        }
    }
}
