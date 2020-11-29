package ru.nsu.fit.markelov.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.controllers.player.ControlBarControl;
import ru.nsu.fit.markelov.controllers.player.KeyEventInfo;
import ru.nsu.fit.markelov.controllers.player.MenuBarControl;
import ru.nsu.fit.markelov.controllers.player.MenuBarObserver;
import ru.nsu.fit.markelov.controllers.player.SubtitlesControl;
import ru.nsu.fit.markelov.controllers.player.SubtitlesObserver;
import ru.nsu.fit.markelov.controllers.player.VlcException;
import ru.nsu.fit.markelov.javafxutil.AlertBuilder;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.F1;
import static javafx.scene.input.KeyCode.F2;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.LEFT;
import static javafx.scene.input.KeyCode.M;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.RIGHT;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCombination.ALT_DOWN;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.LAYOUT_LOADING_ERROR_HEADER;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.VLC_ERROR_HEADER;
import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 * PlayerController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a player scene.
 *
 * @author Oleg Markelov
 */
public class PlayerController implements Controller, SubtitlesObserver, MenuBarObserver {

    private static final String FXML_FILE_NAME = "player.fxml";
    private static final String HOTKEYS_FXML_FILE_NAME = "playerHotkeys.fxml";
    private static final String HOTKEYS_ROW_FXML_FILE_NAME = "playerHotkeysRow.fxml";

    private static final String COLORED_ROW_STYLE_CLASS = "colored-row";

    private static final KeyCodeCombination OPEN_COMBINATION = new KeyCodeCombination(O, CONTROL_DOWN);
    private static final KeyCodeCombination SKIP_LEFT_TEN_COMBINATION = new KeyCodeCombination(LEFT, CONTROL_DOWN);
    private static final KeyCodeCombination SKIP_RIGHT_TEN_COMBINATION = new KeyCodeCombination(RIGHT, CONTROL_DOWN);
    private static final KeyCodeCombination STOP_COMBINATION = new KeyCodeCombination(SPACE, CONTROL_DOWN);
    private static final KeyCodeCombination EXPAND_COMBINATION = new KeyCodeCombination(ENTER, ALT_DOWN);
    private static final KeyCode ESCAPE_CODE = ESCAPE;
    private static final KeyCode MUTE_CODE = M;
    private static final KeyCode HELP_CODE = F1;
    private static final KeyCode TOGGLE_MENU_CODE = F2;
    private static final KeyCode TOGGLE_CONTROLS_CODE = F3;
    private static final KeyCode PAUSE_CODE = SPACE;
    private static final KeyCode SKIP_TO_LEFT_SUBTITLE_CODE = LEFT;
    private static final KeyCode SKIP_TO_CURRENT_SUBTITLE_CODE = DOWN;
    private static final KeyCode SKIP_TO_RIGHT_SUBTITLE_CODE = RIGHT;

    private static final List<KeyEventInfo> HOTKEYS = new ArrayList<>() {{
        add(new KeyEventInfo(OPEN_COMBINATION.getName(), "Open video file"));
        add(new KeyEventInfo(EXPAND_COMBINATION.getName(), "Turn the fullscreen on/off"));
        add(new KeyEventInfo(PAUSE_CODE.getName(), "Pause"));
        add(new KeyEventInfo(STOP_COMBINATION.getName(), "Stop"));
        add(new KeyEventInfo(SKIP_TO_LEFT_SUBTITLE_CODE.getName(), "Skip back to the nearest subtitle"));
        add(new KeyEventInfo(SKIP_TO_RIGHT_SUBTITLE_CODE.getName(), "Skip forward to the nearest subtitle"));
        add(new KeyEventInfo(SKIP_LEFT_TEN_COMBINATION.getName(), "Skip 10 seconds back"));
        add(new KeyEventInfo(SKIP_RIGHT_TEN_COMBINATION.getName(), "Skip 10 seconds forward"));
        add(new KeyEventInfo(SKIP_TO_CURRENT_SUBTITLE_CODE.getName(), "Replay from the current subtitle"));
        add(new KeyEventInfo(ESCAPE_CODE.getName(), "Close the translation popup/Turn the fullscreen off"));
        add(new KeyEventInfo(MUTE_CODE.getName(), "Mute"));
        add(new KeyEventInfo(HELP_CODE.getName(), "Show help menu"));
        add(new KeyEventInfo(TOGGLE_MENU_CODE.getName(), "Pin/unpin the menu bar"));
        add(new KeyEventInfo(TOGGLE_CONTROLS_CODE.getName(), "Pin/unpin the control bar"));
    }};

    @FXML private StackPane root;
    @FXML private ImageView videoImageView;
    @FXML private GridPane mainGridPane;

    @FXML private Group subtitlesGroup;
    @FXML private TextFlow subtitlesTextFlow;

    @FXML private Pane translationPane;
    @FXML private Group translationGroup;
    @FXML private TextFlow translationTextFlow;
    @FXML private ImageView translationSpinnerImageView;

    @FXML private Pane tooltipPane;
    @FXML private Group tooltipGroup;
    @FXML private TextFlow tooltipTextFlow;

    @FXML private GridPane controlsGridPane;
    @FXML private HBox controlsDaemonHBox;

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
    @FXML private ToggleButton controlsToggleButton;
    @FXML private ToggleButton soundToggleButton;
    @FXML private Button expandButton;

    @FXML private Label currentTimeLabel;
    @FXML private Label entireTimeLabel;

    @FXML private StackPane menuBarStackPane;
    @FXML private HBox menuBarDaemonHBox;
    @FXML private ToggleButton menuBarToggleButton;
    @FXML private MenuBar menuBar;
    @FXML private Menu audioMenu;
    @FXML private Menu subtitlesMenu;
    @FXML private Menu sourceLanguageMenu;
    @FXML private Menu targetLanguageMenu;
    @FXML private Menu helpMenu;
    @FXML private MenuItem fileOpenItem;
    @FXML private MenuItem fileCloseItem;
    @FXML private MenuItem helpHotkeysItem;
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
    public PlayerController(SceneManager sceneManager, FileChooserManager fileChooserManager)
        throws IllegalInputException, VlcException
    {
        this.sceneManager = requireNonNull(sceneManager);
        this.fileChooserManager = requireNonNull(fileChooserManager);

        try {
            mediaPlayerFactory = new MediaPlayerFactory();
            embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        } catch (Throwable e) {
            throw new VlcException(e);
        }

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
                    mediaPlayer.audio().setMute(!soundToggleButton.isSelected());
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
                Platform.runLater(() -> new AlertBuilder()
                    .setHeaderText(VLC_ERROR_HEADER).setOwner(sceneManager.getWindowOwner())
                    .build().showAndWait()
                );
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
        videoImageView.setOnMouseClicked(mouseEvent -> {
            if (!subtitlesControl.isTranslationBarVisible()) {
                controlBarControl.onPausePressed();
            }

            subtitlesControl.hideTranslationBar();
        });

        subtitlesControl = new SubtitlesControl(sceneManager, this, subtitlesGroup,
            subtitlesTextFlow, translationPane, translationGroup, translationTextFlow,
            translationSpinnerImageView, tooltipPane, tooltipGroup, tooltipTextFlow);

        controlBarControl = new ControlBarControl(sceneManager, embeddedMediaPlayer,
            subtitlesControl, mainGridPane, controlsGridPane, controlsDaemonHBox, slider,
            leftControlBox, centerControlBox, pauseButton, stopButton, skipLeftTenButton,
            skipRightTenButton, skipLeftButton, skipCurrentButton, skipRightButton,
            controlsToggleButton, soundToggleButton, expandButton, currentTimeLabel, entireTimeLabel);

        menuBarControl = new MenuBarControl(this, fileChooserManager, embeddedMediaPlayer,
            subtitlesControl, controlBarControl, menuBarStackPane, menuBarDaemonHBox,
            menuBarToggleButton, menuBar, audioMenu, subtitlesMenu, sourceLanguageMenu,
            targetLanguageMenu, helpMenu, fileOpenItem, fileCloseItem, helpHotkeysItem);
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
    public void runAfterSceneSet() {
        sceneManager.getScene().setOnKeyReleased(this::onKeyReleased);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        subtitlesControl.close();
        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();
        sceneManager.getScene().setOnKeyReleased(null);
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

    @Override
    public void onHotkeysClicked() {
        try {
            GridPane contentGridPane = (GridPane) sceneManager.loadFXML(HOTKEYS_FXML_FILE_NAME);

            for (int i = 0; i < HOTKEYS.size(); i++) {
                GridPane tmpGridPane = (GridPane) sceneManager.loadFXML(HOTKEYS_ROW_FXML_FILE_NAME);

                HBox hotkeyHBox = (HBox) tmpGridPane.getChildren().get(0);
                HBox descriptionHBox = (HBox) tmpGridPane.getChildren().get(1);

                if (i % 2 == 0) {
                    hotkeyHBox.getStyleClass().add(COLORED_ROW_STYLE_CLASS);
                    descriptionHBox.getStyleClass().add(COLORED_ROW_STYLE_CLASS);
                }

                ((Text) hotkeyHBox.getChildren().get(0)).setText(HOTKEYS.get(i).getName());
                ((Text) descriptionHBox.getChildren().get(0)).setText(HOTKEYS.get(i).getDescription());

                contentGridPane.add(hotkeyHBox, 0, i + 1);
                contentGridPane.add(descriptionHBox, 1, i + 1);
            }

            new AlertBuilder()
                .setAlertType(Alert.AlertType.INFORMATION)
                .setHeaderText("Player Hotkeys")
                .setContent(contentGridPane)
                .setOwner(sceneManager.getWindowOwner())
                .build().showAndWait();
        } catch (IOException | ClassCastException e) {
            new AlertBuilder()
                .setHeaderText(LAYOUT_LOADING_ERROR_HEADER)
                .setException(e)
                .setOwner(sceneManager.getWindowOwner())
                .build().showAndWait();
        }
    }

    private void onKeyReleased(KeyEvent keyEvent) {
        if (OPEN_COMBINATION.match(keyEvent)) {
            chooseFileAndPlay();
        } else if (EXPAND_COMBINATION.match(keyEvent)) {
            controlBarControl.onExpandPressed();
        } else if (keyEvent.getCode() == ESCAPE_CODE) {
            if (subtitlesControl.isTranslationBarVisible()) {
                subtitlesControl.hideTranslationBar();
            } else if (sceneManager.isFullScreen()) {
                controlBarControl.onExpandPressed();
            }
        } else if (keyEvent.getCode() == MUTE_CODE) {
            controlBarControl.fireSoundToggleButton();
        } else if (keyEvent.getCode() == HELP_CODE) {
            menuBarControl.showHelpMenu();
        } else if (keyEvent.getCode() == TOGGLE_MENU_CODE) {
            menuBarControl.fireMenuBarToggleButton();
        } else if (keyEvent.getCode() == TOGGLE_CONTROLS_CODE) {
            controlBarControl.fireControlsToggleButton();
        } else if (initialized) {
            if (STOP_COMBINATION.match(keyEvent)) {
                controlBarControl.onStopPressed();
            } else if (SKIP_LEFT_TEN_COMBINATION.match(keyEvent)) {
                controlBarControl.onSkipLeftTenPressed();
            } else if (SKIP_RIGHT_TEN_COMBINATION.match(keyEvent)) {
                controlBarControl.onSkipRightTenPressed();
            } else if (keyEvent.getCode() == PAUSE_CODE) {
                controlBarControl.onPausePressed();
            } else if (keyEvent.getCode() == SKIP_TO_LEFT_SUBTITLE_CODE) {
                controlBarControl.onSkipLeftPressed();
            } else if (keyEvent.getCode() == SKIP_TO_CURRENT_SUBTITLE_CODE) {
                controlBarControl.onSkipCurrentPressed();
            } else if (keyEvent.getCode() == SKIP_TO_RIGHT_SUBTITLE_CODE) {
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

        initialized = false;
    }
}
