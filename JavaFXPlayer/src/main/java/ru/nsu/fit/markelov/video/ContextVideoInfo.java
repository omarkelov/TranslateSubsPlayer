package ru.nsu.fit.markelov.video;

import java.io.InvalidObjectException;

public class ContextVideoInfo {

    private Long contextId;
    private String videoFilePath;
    private Integer audioChannel;
    private Long startTime;
    private Long endTime;

    public ContextVideoInfo(Long contextId, String videoFilePath, Integer audioChannel, Long startTime, Long endTime) {
        this.contextId = contextId;
        this.videoFilePath = videoFilePath;
        this.audioChannel = audioChannel;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void validate() throws InvalidObjectException {
        if (contextId == null)
            throw new InvalidObjectException("'contextId' parameter is not present");
        if (videoFilePath == null)
            throw new InvalidObjectException("'videoFilePath' parameter is not present");
        if (audioChannel == null)
            throw new InvalidObjectException("'audioChannel' parameter is not present");
        if (startTime == null)
            throw new InvalidObjectException("'startTime' parameter is not present");
        if (endTime == null)
            throw new InvalidObjectException("'endTime' parameter is not present");
    }

    public Long getContextId() {
        return contextId;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public Integer getAudioChannel() {
        return audioChannel;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }
}
