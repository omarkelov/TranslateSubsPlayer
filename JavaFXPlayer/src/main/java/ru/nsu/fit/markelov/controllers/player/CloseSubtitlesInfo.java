package ru.nsu.fit.markelov.controllers.player;

public class CloseSubtitlesInfo {

    private final Long leftSubtitleStartTime;
    private final Long currentSubtitleStartTime;
    private final Long rightSubtitleStartTime;

    public CloseSubtitlesInfo(Long leftSubtitleStartTime, Long currentSubtitleStartTime, Long rightSubtitleStartTime) {
        this.leftSubtitleStartTime = leftSubtitleStartTime;
        this.currentSubtitleStartTime = currentSubtitleStartTime;
        this.rightSubtitleStartTime = rightSubtitleStartTime;
    }

    /**
     * Returns left.
     *
     * @return left.
     */
    public Long getLeftSubtitleStartTime() {
        return leftSubtitleStartTime;
    }

    /**
     * Returns current.
     *
     * @return current.
     */
    public Long getCurrentSubtitleStartTime() {
        return currentSubtitleStartTime;
    }

    /**
     * Returns right.
     *
     * @return right.
     */
    public Long getRightSubtitleStartTime() {
        return rightSubtitleStartTime;
    }
}
