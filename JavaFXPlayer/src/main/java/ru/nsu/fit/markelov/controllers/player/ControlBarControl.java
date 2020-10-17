package ru.nsu.fit.markelov.controllers.player;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import ru.nsu.fit.markelov.managers.SceneManager;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.Locale;

public class ControlBarControl {

    private static final String PLAY_CLASSNAME = "control-button-play";
    private static final String PAUSE_CLASSNAME = "control-button-pause";
    private static final String COLLAPSE_CLASSNAME = "control-button-collapse";
    private static final String EXPAND_CLASSNAME = "control-button-expand";

    private static final String SLIDER_STYLE_FORMAT =
        "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
            + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    /** To avoid flickering the subtitles bar after skipping during a pause */
    private static final int EXTRA_TIME_SKIP = 15;

    private final SceneManager sceneManager;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private final SubtitlesControl subtitlesControl;

    private final Slider slider;

    private final HBox leftControlBox;
    private final HBox centerControlBox;

    private final Button pauseButton;
    private final Button stopButton;
    private final Button skipLeftButton;
    private final Button skipRightButton;
    private final Button soundButton;
    private final Button expandButton;

    private final Label currentTimeLabel;
    private final Label entireTimeLabel;

    public ControlBarControl(SceneManager sceneManager, EmbeddedMediaPlayer embeddedMediaPlayer,
                             SubtitlesControl subtitlesControl, Slider slider, HBox leftControlBox,
                             HBox centerControlBox, Button pauseButton, Button stopButton,
                             Button skipLeftButton, Button skipRightButton, Button soundButton,
                             Button expandButton, Label currentTimeLabel, Label entireTimeLabel) {
        this.sceneManager = sceneManager;
        this.embeddedMediaPlayer = embeddedMediaPlayer;
        this.subtitlesControl = subtitlesControl;
        this.slider = slider;
        this.leftControlBox = leftControlBox;
        this.centerControlBox = centerControlBox;
        this.pauseButton = pauseButton;
        this.stopButton = stopButton;
        this.skipLeftButton = skipLeftButton;
        this.skipRightButton = skipRightButton;
        this.soundButton = soundButton;
        this.expandButton = expandButton;
        this.currentTimeLabel = currentTimeLabel;
        this.entireTimeLabel = entireTimeLabel;

        slider.setOnMousePressed(mouseEvent -> onSliderPressedOrDragged());
        slider.setOnMouseDragged(mouseEvent -> onSliderPressedOrDragged());
        slider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage =
                (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin()) * 100.0;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        }, slider.valueProperty(), slider.minProperty(), slider.maxProperty()));

        pauseButton.setOnAction(actionEvent -> onPausePressed());
        stopButton.setOnAction(actionEvent -> onStopPressed());
        skipLeftButton.setOnAction(actionEvent -> onSkipLeftPressed());
        skipRightButton.setOnAction(actionEvent -> onSkipRightPressed());
        expandButton.setOnAction(actionEvent -> onExpandPressed());

        currentTimeLabel.textProperty().bind(Bindings.createStringBinding(() ->
            formatTime((long) slider.getValue()), slider.valueProperty()));

        entireTimeLabel.textProperty().bind(Bindings.createStringBinding(() ->
            formatTime((long) slider.getMax()), slider.maxProperty()));
    }

    public void init() {
        initControlBoxes();
        initSlider();
    }

    public void dispose() {
        disposeControlBoxes();
        disposeSlider();
    }

    public double getSliderValue() {
        return slider.getValue();
    }

    public void setSliderValue(double value) {
        slider.setValue(value);
    }

    private void initSlider() {
        slider.setMax(embeddedMediaPlayer.status().length() - 1);
        slider.setDisable(false);
    }

    private void disposeSlider() {
        slider.setDisable(true);
        slider.setValue(0);
        slider.setMax(1);
    }

    private void initControlBoxes() {
        leftControlBox.setDisable(false);
        centerControlBox.setDisable(false);
    }

    private void disposeControlBoxes() {
        leftControlBox.setDisable(true);
        centerControlBox.setDisable(true);
    }

    public void onSliderPressedOrDragged() {
        long newTime = (long) slider.getValue();

        subtitlesControl.setTime(newTime);
        embeddedMediaPlayer.controls().setTime(newTime);
    }

    public void onPausePressed() {
        onPausePressed(embeddedMediaPlayer.status().isPlaying());
    }

    public void onPausePressed(boolean pause) {
        if (!pause) {
            subtitlesControl.hideTranslationBar();
        }

        replaceClassName(pauseButton.getStyleClass(), pause, PAUSE_CLASSNAME, PLAY_CLASSNAME);

        embeddedMediaPlayer.controls().setPause(pause);
    }

    public void onStopPressed() {
        if (embeddedMediaPlayer.status().isPlaying()) {
            onPausePressed(true);
        }
        embeddedMediaPlayer.controls().setTime(0);
        slider.setValue(slider.getMin());
        subtitlesControl.setTime(0);
    }

    public void onSkipLeftPressed() {
        skipTo(subtitlesControl.getLeftSubtitleTime());
    }

    public void onSkipCurrentPressed() {
        skipTo(subtitlesControl.getCurrentSubtitleTime());
    }

    public void onSkipRightPressed() {
        skipTo(subtitlesControl.getRightSubtitleTime());
    }

    private void skipTo(Long newTime) {
        if (newTime != null) {
            slider.setValue(newTime + EXTRA_TIME_SKIP);
            onSliderPressedOrDragged();
        }
    }

    public void onExpandPressed() {
        replaceClassName(expandButton.getStyleClass(),
            sceneManager.isFullScreen(), COLLAPSE_CLASSNAME, EXPAND_CLASSNAME);

        sceneManager.toggleFullScreen();
    }

    private void replaceClassName(ObservableList<String> classNames, boolean cond,
                                  String removeClassName, String addClassName) {
        classNames.removeIf(className -> className.equals(cond ? removeClassName : addClassName));
        classNames.add(!cond ? removeClassName : addClassName);
    }

    private String formatTime(long milliseconds) {
        return String.format("%02d:%02d:%02d",
            milliseconds / 3600000, (milliseconds / 60000) % 60, (milliseconds / 1000) % 60);
    }
}
