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
import javax.persistence.Transient;
import java.util.List;

@Entity
@Table(name = "raw_movies")
@Getter @Setter @NoArgsConstructor
public class RawMovie {
    // todo @Unique
    // todo @NotNull
    // todo @Column(length = ?)

    public interface OnlyId {
        Long getId();
    }

    public interface OnlyIdAndVideoFileName {
        Long getId();
        String getVideoFilePath();
    }

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String hashSum;

    @Expose
    @Column(length = 1024)
    private String videoFilePath;

    @Expose
    @Column(name = "_lines", length = 256 * 1024)
    private String lines;

    @Expose
    @Transient
    private List<RawPhrase> phrases;

    public RawMovie(Long userId, String hashSum, String videoFilePath, String lines) {
        this.userId = userId;
        this.hashSum = hashSum;
        this.videoFilePath = videoFilePath;
        this.lines = lines;
    }

    public RawMovie(OnlyIdAndVideoFileName onlyIdAndVideoFileName) {
        id = onlyIdAndVideoFileName.getId();
        videoFilePath = onlyIdAndVideoFileName.getVideoFilePath();
    }
}
