package ru.nsu.fit.markelov.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
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
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.controllers.player.ControlBarControl;
import ru.nsu.fit.markelov.controllers.player.SubtitlesControl;
import ru.nsu.fit.markelov.controllers.player.SubtitlesObserver;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCombination.ALT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 * PlayerController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a player scene.
 *
 * @author Oleg Markelov
 */
public class PlayerController implements Controller, SubtitlesObserver {

    private static final String FXML_FILE_NAME = "player.fxml";

    private static final KeyCodeCombination ON_OPEN_KEYS = new KeyCodeCombination(O, SHORTCUT_DOWN);
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

    @FXML private Slider slider;

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

    private final ToggleGroup audioToggleGroup = new ToggleGroup();
    private final ToggleGroup subtitlesToggleGroup = new ToggleGroup();

    private final SceneManager sceneManager;
    private final FileChooserManager fileChooserManager;

    private File videoFile;

    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;

    private boolean initialized = false;

    private SubtitlesControl subtitlesControl;
    private ControlBarControl controlBarControl;

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
                        controlBarControl.onStopPressed();
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
                subtitlesControl.setTime(newTime);

                Platform.runLater(() -> controlBarControl.setSliderValue(newTime));
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                Platform.runLater(sceneManager::showDefaultError);
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

        videoImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> subtitlesControl.hideTranslationBar());

        subtitlesControl = new SubtitlesControl(sceneManager, this, subtitlesGroup,
            subtitlesTextFlow, translationPane, translationGroup, translationTextFlow);

        controlBarControl = new ControlBarControl(sceneManager, embeddedMediaPlayer,
            subtitlesControl, slider, leftControlBox, centerControlBox, pauseButton, stopButton,
            skipLeftButton, skipRightButton, soundButton, expandButton, currentTimeLabel, entireTimeLabel);
    }

    @Override
    public void onSubtitlesTextPressed() {
        if (embeddedMediaPlayer.status().isPlaying()) {
            controlBarControl.onPausePressed(true);
        }
    }

    private void chooseFileAndPlay() {
        boolean isPlaying = embeddedMediaPlayer.status().isPlaying();
        if (isPlaying) {
            controlBarControl.onPausePressed(true);
        }

        File file = fileChooserManager.chooseVideoFile();
        if (file != null) {
            disposePlaying();

            if (!sceneManager.isFullScreen()) {
                controlBarControl.onExpandPressed();
            }

            embeddedMediaPlayer.media().play(file.getAbsolutePath());

            videoFile = file;
        }

        if (isPlaying) {
            controlBarControl.onPausePressed(false);
        }
    }

    private void initPlayingIfNot(MediaPlayer mediaPlayer) {
        if (!initialized) {
            initialized = true;

            sceneManager.setTitle(videoFile.getName());
            controlBarControl.onPausePressed(false);

            initAudioMenu(mediaPlayer);
            initSubtitlesMenu(mediaPlayer);

            controlBarControl.init();
        }
    }

    private void disposePlaying() {
        controlBarControl.onPausePressed(true);
        embeddedMediaPlayer.controls().stop();
        videoImageView.setImage(null);

        disposeAudioMenu();
        disposeSubtitlesMenu();
        subtitlesControl.disposeSubtitles();

        controlBarControl.dispose();

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
        subtitlesControl.setCurrentSubtitlesMenuItem(disabledRadioItem);
        disabledRadioItem.setOnAction(actionEvent -> {
            subtitlesControl.disposeSubtitles();
            subtitlesControl.setCurrentSubtitlesMenuItem(disabledRadioItem);
        });
        disabledRadioItem.setMnemonicParsing(false);
        subtitlesMenu.getItems().add(disabledRadioItem);

        MenuItem subtitlesOpenItem = new MenuItem("Open .srt-file");
        subtitlesOpenItem.getStyleClass().add("italic");
        subtitlesOpenItem.setOnAction(actionEvent -> {
            boolean isPlaying = embeddedMediaPlayer.status().isPlaying();
            if (isPlaying) {
                controlBarControl.onPausePressed(true);
            }

            File file = fileChooserManager.chooseSubtitlesFile();
            if (file != null) {
                RadioMenuItem radioMenuItem = new RadioMenuItem(file.getName());
                radioMenuItem.setToggleGroup(subtitlesToggleGroup);
                radioMenuItem.setSelected(true);
                radioMenuItem.setOnAction(fileActionEvent -> subtitlesControl.initSubtitles(file.getAbsolutePath(), radioMenuItem, (long) controlBarControl.getSliderValue()));
                radioMenuItem.setMnemonicParsing(false);
                subtitlesMenu.getItems().add(subtitlesMenu.getItems().size() - 1, radioMenuItem);

                subtitlesControl.initSubtitles(file.getAbsolutePath(), radioMenuItem, (long) controlBarControl.getSliderValue());
            }

            if (isPlaying) {
                controlBarControl.onPausePressed(false);
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
                    radioMenuItem.setOnAction(fileActionEvent -> subtitlesControl.initSubtitles(
                        subtitlesPath.toString(), radioMenuItem, (long) controlBarControl.getSliderValue()));
                    radioMenuItem.setMnemonicParsing(false);
                    subtitlesMenu.getItems().add(subtitlesMenu.getItems().size() - 1, radioMenuItem);

                    if (disabled) {
                        subtitlesControl.initSubtitles(
                            subtitlesPath.toString(), radioMenuItem, (long) controlBarControl.getSliderValue());
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

    private void onKeyReleased(KeyEvent keyEvent) {
        if (ON_OPEN_KEYS.match(keyEvent)) {
            chooseFileAndPlay();
        } else if (ON_STOP_KEYS.match(keyEvent)) {
            controlBarControl.onStopPressed();
        } else if (ON_EXPAND_KEYS.match(keyEvent)) {
            controlBarControl.onExpandPressed();
        } else if (keyEvent.getCode() == ESCAPE) {
            if (subtitlesControl.isTranslationBarVisible()) {
                subtitlesControl.hideTranslationBar();
            } else if (sceneManager.isFullScreen()) {
                controlBarControl.onExpandPressed();
            }
        } else if (keyEvent.getCode() == SPACE) {
            controlBarControl.onPausePressed();
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
}
