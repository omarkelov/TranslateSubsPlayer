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
    @Column(length = 1024)
    private String context;

    @Expose
    private String link;

    @Expose
    @Transient
    private List<Phrase> phrases;

    public Context(String context, String link, List<Phrase> phrases) {
        this.context = context;
        this.link = link;
        this.phrases = phrases;
    }
}
