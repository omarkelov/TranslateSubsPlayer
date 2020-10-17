package ru.nsu.fit.markelov.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.controllers.player.ControlBarControl;
import ru.nsu.fit.markelov.controllers.player.MenuBarControl;
import ru.nsu.fit.markelov.controllers.player.MenuBarObserver;
import ru.nsu.fit.markelov.controllers.player.SubtitlesControl;
import ru.nsu.fit.markelov.controllers.player.SubtitlesObserver;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.RIGHT;
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
public class PlayerController implements Controller, SubtitlesObserver, MenuBarObserver {

    private static final String FXML_FILE_NAME = "player.fxml";

    private static final KeyCodeCombination ON_OPEN_KEYS = new KeyCodeCombination(O, SHORTCUT_DOWN);
    private static final KeyCodeCombination ON_SKIP_LEFT_TEN_KEYS = new KeyCodeCombination(LEFT, SHORTCUT_DOWN);
    private static final KeyCodeCombination ON_SKIP_RIGHT_TEN_KEYS = new KeyCodeCombination(RIGHT, SHORTCUT_DOWN);
    private static final KeyCodeCombination ON_STOP_KEYS = new KeyCodeCombination(SPACE, SHORTCUT_DOWN);
    private static final KeyCodeCombination ON_EXPAND_KEYS = new KeyCodeCombination(ENTER, ALT_DOWN);

    @FXML private StackPane root;
    @FXML private ImageView videoImageView;

    @FXML private Group subtitlesGroup;
    @FXML private TextFlow subtitlesTextFlow;

    @FXML private Pane translationPane;
    @FXML private Group translationGroup;
    @FXML private TextFlow translationTextFlow;

    @FXML private Slider slider;

    @FXML private HBox leftControlBox;
    @FXML private HBox centerControlBox;

    @FXML private Button pauseButton;
    @FXML private Button stopButton;
    @FXML private Button skipLeftTenButton;
    @FXML private Button skipRightTenButton;
    @FXML private Button skipLeftButton;
    @FXML private Button skipCurrentButton;
    @FXML private Button skipRightButton;
    @FXML private Button soundButton;
    @FXML private Button expandButton;

    @FXML private Label currentTimeLabel;
    @FXML private Label entireTimeLabel;

    @FXML private Menu audioMenu;
    @FXML private Menu subtitlesMenu;
    @FXML private MenuItem fileOpenItem;
    @FXML private MenuItem fileCloseItem;
    @FXML private MenuItem helpAboutItem;

    private final SceneManager sceneManager;
    private final FileChooserManager fileChooserManager;

    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;

    private boolean initialized = false;

    private SubtitlesControl subtitlesControl;
    private ControlBarControl controlBarControl;
    private MenuBarControl menuBarControl;

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
                        mediaPlayer.audio().setTrack(menuBarControl.getSelectedAudioTrack());
                    });
                } else {
                    Platform.runLater(() -> initPlayingIfNot());
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

    private void initPlayingIfNot() {
        if (!initialized) {
            initialized = true;

            sceneManager.setTitle(menuBarControl.getVideoFile().getName());
            controlBarControl.onPausePressed(false);

            controlBarControl.init();
            menuBarControl.init();
        }
    }

    @FXML
    private void initialize() {
        embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(videoImageView));

        videoImageView.fitWidthProperty().bind(root.widthProperty());
        videoImageView.fitHeightProperty().bind(root.heightProperty());
        videoImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> subtitlesControl.hideTranslationBar());

        subtitlesControl = new SubtitlesControl(sceneManager, this, subtitlesGroup,
            subtitlesTextFlow, translationPane, translationGroup, translationTextFlow);

        controlBarControl = new ControlBarControl(sceneManager, embeddedMediaPlayer,
            subtitlesControl, slider, leftControlBox, centerControlBox, pauseButton, stopButton,
            skipLeftTenButton, skipRightTenButton, skipLeftButton, skipCurrentButton, skipRightButton,
            soundButton, expandButton, currentTimeLabel, entireTimeLabel);

        menuBarControl = new MenuBarControl(this, fileChooserManager, embeddedMediaPlayer,
            subtitlesControl, controlBarControl, audioMenu, subtitlesMenu, fileOpenItem, fileCloseItem);
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

    @Override
    public void onSubtitlesTextPressed() {
        if (embeddedMediaPlayer.status().isPlaying()) {
            controlBarControl.onPausePressed(true);
        }
    }

    @Override
    public void onFileClicked() {
        chooseFileAndPlay();
    }

    @Override
    public void onClosedClicked() {
        disposePlaying();
    }

    private void onKeyReleased(KeyEvent keyEvent) {
        if (ON_OPEN_KEYS.match(keyEvent)) {
            chooseFileAndPlay();
        } else if (ON_EXPAND_KEYS.match(keyEvent)) {
            controlBarControl.onExpandPressed();
        } else if (keyEvent.getCode() == ESCAPE) {
            if (subtitlesControl.isTranslationBarVisible()) {
                subtitlesControl.hideTranslationBar();
            } else if (sceneManager.isFullScreen()) {
                controlBarControl.onExpandPressed();
            }
        } else if (initialized) {
            if (ON_STOP_KEYS.match(keyEvent)) {
                controlBarControl.onStopPressed();
            } else if (ON_SKIP_LEFT_TEN_KEYS.match(keyEvent)) {
                controlBarControl.onSkipLeftTenPressed();
            } else if (ON_SKIP_RIGHT_TEN_KEYS.match(keyEvent)) {
                controlBarControl.onSkipRightTenPressed();
            } else if (keyEvent.getCode() == SPACE) {
                controlBarControl.onPausePressed();
            } else if (keyEvent.getCode() == LEFT) {
                controlBarControl.onSkipLeftPressed();
            } else if (keyEvent.getCode() == DOWN) {
                controlBarControl.onSkipCurrentPressed();
            } else if (keyEvent.getCode() == RIGHT) {
                controlBarControl.onSkipRightPressed();
            }
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

            menuBarControl.setVideoFile(file);
        }

        if (isPlaying) {
            controlBarControl.onPausePressed(false);
        }
    }

    private void disposePlaying() {
        controlBarControl.onPausePressed(true);
        embeddedMediaPlayer.controls().stop();
        videoImageView.setImage(null);

        subtitlesControl.disposeSubtitles();
        controlBarControl.dispose();
        menuBarControl.dispose();

        sceneManager.setDefaultTitle();
        menuBarControl.setVideoFile(null);

        root.requestFocus();

        initialized = false;
    }
}
