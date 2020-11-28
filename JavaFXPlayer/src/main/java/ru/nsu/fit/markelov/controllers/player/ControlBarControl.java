package ru.nsu.fit.markelov.controllers.player;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import ru.nsu.fit.markelov.managers.SceneManager;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.Locale;

public class ControlBarControl {

    private static final double MENU_BAR_DAEMON_BOX_MAX_HEIGHT = 10;
    private static final double MENU_BAR_DAEMON_BOX_HEIGHT_FACTOR = 0.8d;

    private static final String PLAY_CLASSNAME = "control-button-play";
    private static final String PAUSE_CLASSNAME = "control-button-pause";
    private static final String COLLAPSE_CLASSNAME = "control-button-collapse";
    private static final String EXPAND_CLASSNAME = "control-button-expand";

    private static final String SLIDER_STYLE_FORMAT =
        "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
            + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    private static final int SKIP_TIME = 10000;
    /** To avoid flickering the subtitles bar after skipping during a pause */
    private static final int EXTRA_TIME_SKIP = 15;

    private final SceneManager sceneManager;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private final SubtitlesControl subtitlesControl;

    private final GridPane mainGridPane;
    private final GridPane controlsGridPane;
    private final HBox controlsDaemonHBox;

    private final Slider slider;

    private final HBox leftControlBox;
    private final HBox centerControlBox;

    private final Button pauseButton;
    private final Button stopButton;
    private final Button skipLeftTenButton;
    private final Button skipRightTenButton;
    private final Button skipLeftButton;
    private final Button skipCurrentButton;
    private final Button skipRightButton;
    private final ToggleButton controlsToggleButton;
    private final ToggleButton soundToggleButton;
    private final Button expandButton;

    private final Label currentTimeLabel;
    private final Label entireTimeLabel;

    public ControlBarControl(SceneManager sceneManager, EmbeddedMediaPlayer embeddedMediaPlayer,
                             SubtitlesControl subtitlesControl, GridPane mainGridPane,
                             GridPane controlsGridPane, HBox controlsDaemonHBox, Slider slider,
                             HBox leftControlBox, HBox centerControlBox, Button pauseButton,
                             Button stopButton, Button skipLeftTenButton, Button skipRightTenButton,
                             Button skipLeftButton, Button skipCurrentButton,
                             Button skipRightButton, ToggleButton controlsToggleButton,
                             ToggleButton soundToggleButton, Button expandButton,
                             Label currentTimeLabel, Label entireTimeLabel)
    {
        this.sceneManager = sceneManager;
        this.embeddedMediaPlayer = embeddedMediaPlayer;
        this.subtitlesControl = subtitlesControl;
        this.mainGridPane = mainGridPane;
        this.controlsGridPane = controlsGridPane;
        this.controlsDaemonHBox = controlsDaemonHBox;
        this.slider = slider;
        this.leftControlBox = leftControlBox;
        this.centerControlBox = centerControlBox;
        this.pauseButton = pauseButton;
        this.stopButton = stopButton;
        this.skipLeftTenButton = skipLeftTenButton;
        this.skipRightTenButton = skipRightTenButton;
        this.skipLeftButton = skipLeftButton;
        this.skipCurrentButton = skipCurrentButton;
        this.skipRightButton = skipRightButton;
        this.controlsToggleButton = controlsToggleButton;
        this.soundToggleButton = soundToggleButton;
        this.expandButton = expandButton;
        this.currentTimeLabel = currentTimeLabel;
        this.entireTimeLabel = entireTimeLabel;

        activateBindings();

        slider.setOnMousePressed(mouseEvent -> onSliderPressedOrDragged());
        slider.setOnMouseDragged(mouseEvent -> onSliderPressedOrDragged());
        slider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage =
                (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin()) * 100.0;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        }, slider.valueProperty(), slider.minProperty(), slider.maxProperty()));

        pauseButton.setOnAction(actionEvent -> onPausePressed());
        stopButton.setOnAction(actionEvent -> onStopPressed());
        skipLeftTenButton.setOnAction(actionEvent -> onSkipLeftTenPressed());
        skipRightTenButton.setOnAction(actionEvent -> onSkipRightTenPressed());
        skipLeftButton.setOnAction(actionEvent -> onSkipLeftPressed());
        skipCurrentButton.setOnAction(actionEvent -> onSkipCurrentPressed());
        skipRightButton.setOnAction(actionEvent -> onSkipRightPressed());
        soundToggleButton.setOnAction(actionEvent -> onSoundPressed());
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

    public void fireControlsToggleButton() {
        controlsToggleButton.fire();
        controlsGridPane.setVisible(controlsToggleButton.isSelected());
    }

    public void fireSoundToggleButton() {
        soundToggleButton.fire();
    }

    private void activateBindings() {
        controlsGridPane.managedProperty().bind(controlsGridPane.visibleProperty());

        controlsDaemonHBox.visibleProperty().bind(controlsGridPane.visibleProperty().not());
        controlsDaemonHBox.managedProperty().bind(controlsGridPane.managedProperty().not());
        controlsDaemonHBox.prefHeightProperty().bind(Bindings.max(MENU_BAR_DAEMON_BOX_MAX_HEIGHT,
            mainGridPane.vgapProperty().multiply(MENU_BAR_DAEMON_BOX_HEIGHT_FACTOR)));
        controlsDaemonHBox.setOnMouseEntered(mouseEvent -> controlsGridPane.setVisible(true));

        controlsGridPane.setOnMouseExited(mouseEvent -> {
            if (controlsToggleButton.isSelected()) {
                return;
            }

            controlsGridPane.setVisible(false);
        });
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

    public void onSkipLeftTenPressed() {
        skipTo(embeddedMediaPlayer.status().time() - SKIP_TIME);
    }

    public void onSkipRightTenPressed() {
        skipTo(embeddedMediaPlayer.status().time() + SKIP_TIME);
    }

    public void onSkipLeftPressed() {
        skipToSubtitle(subtitlesControl.getLeftSubtitleTime());
    }

    public void onSkipCurrentPressed() {
        skipToSubtitle(subtitlesControl.getCurrentSubtitleTime());
        onPausePressed(false);
    }

    public void onSkipRightPressed() {
        skipToSubtitle(subtitlesControl.getRightSubtitleTime());
    }

    private void skipToSubtitle(Long startTime) {
        if (startTime != null) {
            skipTo(startTime + EXTRA_TIME_SKIP);
        }
    }

    private void skipTo(long newTime) {
        if (newTime < slider.getMin()) {
            newTime = (long) slider.getMin();
        } else if (newTime > slider.getMax()) {
            newTime = (long) slider.getMax();
        }

        slider.setValue(newTime);
        onSliderPressedOrDragged();
    }

    private void onSoundPressed() {
        if (!embeddedMediaPlayer.status().isPlaying()) {
            return;
        }

        embeddedMediaPlayer.audio().setMute(!soundToggleButton.isSelected());
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
