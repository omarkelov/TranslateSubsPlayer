package ru.nsu.fit.markelov.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.subtitles.BOMSrtParser;
import ru.nsu.fit.markelov.subtitles.JavaFxSubtitles;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.subs.Spus;
import uk.co.caprica.vlcj.subs.handler.SpuHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCombination.ALT_DOWN;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;
import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 * PlayerController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a player scene.
 *
 * @author Oleg Markelov
 */
public class PlayerController implements Controller {

    private static final String FXML_FILE_NAME = "player.fxml";

    private static final String PLAY_CLASSNAME = "control-button-play";
    private static final String PAUSE_CLASSNAME = "control-button-pause";
    private static final String COLLAPSE_CLASSNAME = "control-button-collapse";
    private static final String EXPAND_CLASSNAME = "control-button-expand";

    private static final String SLIDER_STYLE_FORMAT =
        "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
            + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    private static final KeyCodeCombination ON_OPEN_KEYS = new KeyCodeCombination(O, CONTROL_DOWN);
    private static final KeyCodeCombination ON_STOP_KEYS = new KeyCodeCombination(P, ALT_DOWN);
    private static final KeyCodeCombination ON_EXPAND_KEYS = new KeyCodeCombination(ENTER, ALT_DOWN);

    @FXML private StackPane root;
    @FXML private ImageView videoImageView;
    @FXML private GridPane gridPane;
    @FXML private Group subtitlesGroup;
    @FXML private TextFlow subtitlesTextFlow;

    @FXML private Pane translationPane;
    @FXML private Group translationGroup;
    @FXML private TextFlow translationTextFlow;

    @FXML private Menu audioMenu;
    @FXML private Menu subtitlesMenu;
    @FXML private MenuItem fileOpenItem;
    @FXML private MenuItem fileCloseItem;
    @FXML private MenuItem subtitlesAddItem;
    @FXML private MenuItem helpAboutItem;

    @FXML private HBox leftControlBox;
    @FXML private HBox centerControlBox;

    @FXML private Button pauseButton;
    @FXML private Button stopButton;
    @FXML private Button skipLeftButton;
    @FXML private Button skipRightButton;
    @FXML private Button soundButton;
    @FXML private Button expandButton;

    @FXML private Label currentTimeLabel;
    @FXML private Label entireTimeLabel;

    @FXML private Slider slider;

    private final ToggleGroup audioToggleGroup = new ToggleGroup();
    private final ToggleGroup subtitlesToggleGroup = new ToggleGroup();

    private final SceneManager sceneManager;
    private final FileChooserManager fileChooserManager;

    private File videoFile;

    private SpuHandler subtitlesHandler;
    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;

    private RadioMenuItem currentSubtitlesMenuItem;

    private boolean initialized = false;

    /**
     * Creates new PlayerController with specified SceneManager.
     *
     * @param sceneManager       scene manager.
     * @param fileChooserManager file chooser manager.
     * @throws IllegalInputException if one of the input parameters is null.
     */
    public PlayerController(SceneManager sceneManager,
                            FileChooserManager fileChooserManager) throws IllegalInputException {
        this.sceneManager = requireNonNull(sceneManager);
        this.fileChooserManager = requireNonNull(fileChooserManager);

        mediaPlayerFactory = new MediaPlayerFactory();
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        embeddedMediaPlayer.controls().setRepeat(true);

        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

            private boolean finished = false;

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                if (finished) {
                    finished = false;
                    Platform.runLater(() -> {
                        onStopPressed();
                        mediaPlayer.subpictures().setTrack(-1);
                        mediaPlayer.audio().setTrack(
                            (Integer) audioToggleGroup.getSelectedToggle().getUserData());
                    });
                } else {
                    Platform.runLater(() -> initPlayingIfNot(mediaPlayer));
                }
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                finished = true;
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (subtitlesHandler != null) {
                    subtitlesHandler.setTime(newTime);
                }

                Platform.runLater(() -> slider.setValue(newTime));
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                Platform.runLater(() -> sceneManager.showDefaultError());
            }
        });
    }

    @FXML
    private void initialize() {
        embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(videoImageView));

        videoImageView.fitWidthProperty().bind(root.widthProperty());
        videoImageView.fitHeightProperty().bind(root.heightProperty());

        fileOpenItem.setOnAction(actionEvent -> chooseFileAndPlay());
        fileCloseItem.setOnAction(actionEvent -> disposePlaying());

        pauseButton.setOnAction(actionEvent -> onPausePressed());
        stopButton.setOnAction(actionEvent -> onStopPressed());
        expandButton.setOnAction(actionEvent -> onExpandPressed());

        slider.setOnMousePressed(mouseEvent -> onSliderPressedOrDragged());
        slider.setOnMouseDragged(mouseEvent -> onSliderPressedOrDragged());
        slider.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin()) * 100.0;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        }, slider.valueProperty(), slider.minProperty(), slider.maxProperty()));

        currentTimeLabel.textProperty().bind(Bindings.createStringBinding(() ->
            formatTime((long) slider.getValue()), slider.valueProperty()));

        entireTimeLabel.textProperty().bind(Bindings.createStringBinding(() ->
            formatTime((long) slider.getMax()), slider.maxProperty()));

        videoImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> hideTranslationPane());

        subtitlesTextFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getPickResult().getIntersectedNode() == subtitlesTextFlow) {
                hideTranslationPane();
            }
        });
    }

    private void hideTranslationPane() {
        translationGroup.layoutXProperty().unbind();
        translationGroup.layoutYProperty().unbind();
        translationPane.setVisible(false);
    }

    private void chooseFileAndPlay() {
        boolean isPlaying = embeddedMediaPlayer.status().isPlaying();
        if (isPlaying) {
            onPausePressed(true);
        }

        File file = fileChooserManager.chooseVideoFile();
        if (file != null) {
            disposePlaying();

            if (!sceneManager.isFullScreen()) {
                onExpandPressed();
            }

            embeddedMediaPlayer.media().play(file.getAbsolutePath());

            videoFile = file;
        }

        if (isPlaying) {
            onPausePressed(false);
        }
    }

    private void initPlayingIfNot(MediaPlayer mediaPlayer) {
        if (!initialized) {
            initialized = true;

            sceneManager.setTitle(videoFile.getName());
            onPausePressed(false);

            initAudioMenu(mediaPlayer);
            initSubtitlesMenu(mediaPlayer);

            initControlBoxes();
            initSlider(mediaPlayer);
        }
    }

    private void disposePlaying() {
        onPausePressed(true);
        embeddedMediaPlayer.controls().stop();
        videoImageView.setImage(null);

        disposeAudioMenu();
        disposeSubtitlesMenu();
        disposeSubtitles();

        disposeControlBoxes();
        disposeSlider();

        sceneManager.setDefaultTitle();
        videoFile = null;

        root.requestFocus();

        initialized = false;
    }

    private void initAudioMenu(MediaPlayer mediaPlayer) {
        for (TrackDescription trackDescription : mediaPlayer.audio().trackDescriptions()) {
            if (trackDescription.id() == -1) {
                continue;
            }

            RadioMenuItem radioItem = new RadioMenuItem(trackDescription.description());
            radioItem.setToggleGroup(audioToggleGroup);
            radioItem.setSelected(trackDescription.id() == mediaPlayer.audio().track());
            radioItem.setUserData(trackDescription.id());
            radioItem.setOnAction(actionEvent -> mediaPlayer.audio().setTrack(trackDescription.id()));
            radioItem.setMnemonicParsing(false);
            audioMenu.getItems().add(radioItem);
        }

        audioMenu.setDisable(false);
    }

    private void disposeAudioMenu() {
        audioMenu.setDisable(true);
        audioMenu.getItems().clear();
        audioToggleGroup.getToggles().clear();
    }

    private void initSubtitlesMenu(MediaPlayer mediaPlayer) {
        mediaPlayer.subpictures().setTrack(-1); // disable subtitles inside vlc-player

        RadioMenuItem disabledRadioItem = new RadioMenuItem("Disabled");
        disabledRadioItem.setToggleGroup(subtitlesToggleGroup);
        disabledRadioItem.setSelected(true);
        currentSubtitlesMenuItem = disabledRadioItem;
        disabledRadioItem.setOnAction(actionEvent -> {
            disposeSubtitles();
            currentSubtitlesMenuItem = disabledRadioItem;
        });
        disabledRadioItem.setMnemonicParsing(false);
        subtitlesMenu.getItems().add(disabledRadioItem);

        MenuItem subtitlesOpenItem = new MenuItem("Open .srt-file");
        subtitlesOpenItem.getStyleClass().add("italic");
        subtitlesOpenItem.setOnAction(actionEvent -> {
            boolean isPlaying = embeddedMediaPlayer.status().isPlaying();
            if (isPlaying) {
                onPausePressed(true);
            }

            File file = fileChooserManager.chooseSubtitlesFile();
            if (file != null) {
                RadioMenuItem radioMenuItem = new RadioMenuItem(file.getName());
                radioMenuItem.setToggleGroup(subtitlesToggleGroup);
                radioMenuItem.setSelected(true);
                radioMenuItem.setOnAction(fileActionEvent -> initSubtitles(file.getAbsolutePath(), radioMenuItem));
                radioMenuItem.setMnemonicParsing(false);
                subtitlesMenu.getItems().add(subtitlesMenu.getItems().size() - 1, radioMenuItem);

                initSubtitles(file.getAbsolutePath(), radioMenuItem);
            }

            if (isPlaying) {
                onPausePressed(false);
            }
        });
        subtitlesMenu.getItems().add(subtitlesOpenItem);

        try {
            String videoFileName = videoFile.getName().replaceFirst("[.][^.]+$", "");
            Path videoFilePath = Path.of(videoFile.getParent());
            Files
                .walk(videoFilePath)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.startsWith(videoFileName) && fileName.endsWith(".srt");
                })
                .forEach(subtitlesPath -> {
                    boolean disabled = ((RadioMenuItem) subtitlesToggleGroup.getSelectedToggle()).getText().equals("Disabled");

                    RadioMenuItem radioMenuItem = new RadioMenuItem(videoFilePath.relativize(subtitlesPath).toString());
                    radioMenuItem.setToggleGroup(subtitlesToggleGroup);
                    radioMenuItem.setSelected(disabled);
                    radioMenuItem.setOnAction(fileActionEvent -> initSubtitles(subtitlesPath.toString(), radioMenuItem));
                    radioMenuItem.setMnemonicParsing(false);
                    subtitlesMenu.getItems().add(subtitlesMenu.getItems().size() - 1, radioMenuItem);

                    if (disabled) {
                        initSubtitles(subtitlesPath.toString(), radioMenuItem);
                    }
                });
        } catch (Exception e) {
            System.out.println("Could not walk current video file directory: " + e.getMessage());
        }

        subtitlesMenu.setDisable(false);
    }

    private void disposeSubtitlesMenu() {
        subtitlesMenu.setDisable(true);
        subtitlesMenu.getItems().clear();
        subtitlesToggleGroup.getToggles().clear();
    }

    private void initControlBoxes() {
        leftControlBox.setDisable(false);
        centerControlBox.setDisable(false);
    }

    private void disposeControlBoxes() {
        leftControlBox.setDisable(true);
        centerControlBox.setDisable(true);
    }

    private void initSlider(MediaPlayer mediaPlayer) {
        slider.setMax(mediaPlayer.status().length() - 1);
        slider.setDisable(false);
    }

    private void disposeSlider() {
        slider.setDisable(true);
        slider.setValue(0);
        slider.setMax(1);
    }

    private void initSubtitles(String fileName, RadioMenuItem newRadioMenuItem) {
        try (FileReader fileReader = new FileReader(fileName)) {
            Spus subtitleUnits = new BOMSrtParser().parse(fileReader);

            subtitlesHandler = new SpuHandler(subtitleUnits);
            subtitlesHandler.addSpuEventListener(subtitleUnit -> {
                if (subtitleUnit != null && !subtitleUnit.value().toString().isEmpty()) {
                    JavaFxSubtitles javaFxSubtitles =
                        new JavaFxSubtitles(subtitleUnit.value().toString());

                    Platform.runLater(() -> {
                        hideTranslationPane();
                        subtitlesTextFlow.getChildren().clear();
                        subtitlesTextFlow.getChildren().addAll(javaFxSubtitles.getTextList());
                        subtitlesTextFlow.setVisible(true);
                    });
                } else {
                    Platform.runLater(() -> {
                        subtitlesTextFlow.setVisible(false);
                        subtitlesTextFlow.getChildren().clear();
                        hideTranslationPane();
                    });
                }
            });
            subtitlesTextFlow.setVisible(false);
            subtitlesTextFlow.getChildren().clear();
            currentSubtitlesMenuItem = newRadioMenuItem;
            subtitlesHandler.setTime((long) slider.getValue());
        } catch (IOException e) {
            currentSubtitlesMenuItem.setSelected(true);
            sceneManager.showError("File cannot be opened",
                "The next file cannot be opened: " + fileName);
        } catch (Exception e) {
            currentSubtitlesMenuItem.setSelected(true);
            sceneManager.showError("Subtitles cannot be parsed",
                "The next file cannot be parsed: " + fileName);
        }
    }

    private void disposeSubtitles() {
        subtitlesHandler = null;
        subtitlesTextFlow.setVisible(false);
        subtitlesTextFlow.getChildren().clear();
        hideTranslationPane();
    }

    private void onSliderPressedOrDragged() {
        long newTime = (long) slider.getValue();

        if (subtitlesHandler != null) {
            subtitlesHandler.setTime(newTime);
        }

        embeddedMediaPlayer.controls().setTime(newTime);
    }

    private void onPausePressed() {
        onPausePressed(embeddedMediaPlayer.status().isPlaying());
    }

    private void onPausePressed(boolean pause) {
        if (!pause) {
            hideTranslationPane();
        }

        replaceClassName(pauseButton.getStyleClass(), pause, PAUSE_CLASSNAME, PLAY_CLASSNAME);

        embeddedMediaPlayer.controls().setPause(pause);
    }

    private void onStopPressed() {
        if (embeddedMediaPlayer.status().isPlaying()) {
            onPausePressed(true);
        }
        embeddedMediaPlayer.controls().setTime(0);
        slider.setValue(slider.getMin());
        if (subtitlesHandler != null) {
            subtitlesHandler.setTime(0);
        }
    }

    private void onExpandPressed() {
        replaceClassName(expandButton.getStyleClass(),
            sceneManager.isFullScreen(), COLLAPSE_CLASSNAME, EXPAND_CLASSNAME);

        sceneManager.toggleFullScreen();
    }

    private void replaceClassName(ObservableList<String> classNames, boolean cond,
                                  String removeClassName, String addClassName) {
        classNames.removeIf(className -> className.equals(cond ? removeClassName : addClassName));
        classNames.add(!cond ? removeClassName : addClassName);
    }

    private void onKeyReleased(KeyEvent keyEvent) {
        if (ON_OPEN_KEYS.match(keyEvent)) {
            chooseFileAndPlay();
        } else if (ON_STOP_KEYS.match(keyEvent)) {
            onStopPressed();
        } else if (ON_EXPAND_KEYS.match(keyEvent)) {
            onExpandPressed();
        } else if (keyEvent.getCode() == ESCAPE) {
            if (translationPane.isVisible()) {
                hideTranslationPane();
            } else if (sceneManager.isFullScreen()) {
                onExpandPressed();
            }
        } else if (keyEvent.getCode() == SPACE) {
            onPausePressed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAfterSceneSet(Parent root) throws IllegalInputException {
        requireNonNull(root);

        root.setOnKeyReleased(this::onKeyReleased);

        root.requestFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();
    }

    private String formatTime(long milliseconds) {
        return String.format("%02d:%02d:%02d",
            milliseconds / 3600000, (milliseconds / 60000) % 60, (milliseconds / 1000) % 60);
    }
}
