package ru.nsu.fit.subsplayer.database.entities;

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
import java.util.List;

@Entity
@Table(name = "movies")
@Getter @Setter @NoArgsConstructor
public class Movie {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @Expose
    private String name;

    @Expose
    @Transient
    private String href;

    @Expose
    @Transient
    private List<Context> contexts;

    public Movie(Long userId, String name, List<Context> contexts) {
        this.userId = userId;
        this.name = name;
        this.contexts = contexts;
    }
}
