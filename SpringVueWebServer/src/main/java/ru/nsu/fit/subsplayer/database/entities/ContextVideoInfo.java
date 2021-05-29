package ru.nsu.fit.subsplayer.database.entities;

public class ContextVideoInfo {

    private Long contextId;
    private String videoFilePath;
    private Integer audioChannel;
    private Long startTime;
    private Long endTime;

    public ContextVideoInfo(Context context, Movie movie) {
        contextId = context.getId();
        videoFilePath = movie.getVideoFilePath();
        audioChannel = 1; // todo movie.getAudioChannel();
        startTime = context.getStartTime();
        endTime = context.getEndTime();
    }
}
