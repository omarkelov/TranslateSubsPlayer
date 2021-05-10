package ru.nsu.fit.markelov.subtitles;

public class RawMovie {

    private final String hashSum;
    private final String videoFilePath;
    private final String linesJson;

    public RawMovie(String hashSum, String videoFilePath, String linesJson) {
        this.hashSum = hashSum;
        this.videoFilePath = videoFilePath;
        this.linesJson = linesJson;
    }
}
