package ru.nsu.fit.markelov.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
import uk.co.caprica.vlcj.subs.parser.SpuParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCombination.ALT_DOWN;
import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 * PlayerController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a player scene.
 *
 * @author Oleg Markelov
 */
public class PlayerController implements Controller {

    private static final String FXML_FILE_NAME = "player.fxml";
    private static final String INITIAL_TIME = "00:00:00";

    private static final String COLLAPSE_CLASSNAME = "control-button-collapse";
    private static final String EXPAND_CLASSNAME = "control-button-expand";

    private static final KeyCodeCombination ON_EXPAND_KEYS = new KeyCodeCombination(ENTER, ALT_DOWN);

    @FXML private StackPane root;
    @FXML private ImageView videoImageView;
    @FXML private GridPane gridPane;
    @FXML private TextFlow textFlow;

    @FXML private Menu audioMenu;
    @FXML private Menu subtitlesMenu;
    @FXML private MenuItem fileOpenItem;
    @FXML private MenuItem fileCloseItem;
    @FXML private MenuItem subtitlesAddItem;
    @FXML private MenuItem helpAboutItem;

    @FXML private Button playButton;
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

    private SpuHandler subtitlesHandler;
    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;

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

        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                initPlayingIfNot(mediaPlayer);
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (subtitlesHandler != null) {
                    subtitlesHandler.setTime(newTime);
                }

                Platform.runLater(() -> currentTimeLabel.setText(formatTime(newTime)));
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("----- ERROR -----"); // todo show message
            }
        });
    }

    @FXML
    private void initialize() {
        embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(videoImageView));

        videoImageView.fitWidthProperty().bind(root.widthProperty());
        videoImageView.fitHeightProperty().bind(root.heightProperty());

        fileOpenItem.setOnAction(actionEvent -> {
            embeddedMediaPlayer.controls().setPause(true);

            File file = fileChooserManager.chooseVideoFile();
            if (file != null) {
                disposePlaying();

                embeddedMediaPlayer.media().play(file.getAbsolutePath());

                videoFile = file;
            }

            embeddedMediaPlayer.controls().setPause(false);
        });

        fileCloseItem.setOnAction(actionEvent -> disposePlaying());

        expandButton.setOnAction(actionEvent -> onExpandPressed());
    }

    private void initPlayingIfNot(MediaPlayer mediaPlayer) {
        if (!initialized) {
            initAudioMenu(mediaPlayer);
            initSubtitlesMenu(mediaPlayer);
            initTimeLabels(mediaPlayer);

            Platform.runLater(() -> sceneManager.setTitle(videoFile.getName()));

            initialized = true;

            mediaPlayer.submit(() -> { // TODO -hardcode
                mediaPlayer.controls().setPosition(0.042569723f);
                //mediaPlayer.controls().setTime(2 * 60 * 1000 + 47 * 1000);
            });
        }
    }

    private void disposePlaying() {
        embeddedMediaPlayer.controls().stop();
        videoImageView.setImage(null);

        disposeAudioMenu();
        disposeSubtitlesMenu();
        disposeSubtitles();
        disposeTimeLabels();

        sceneManager.setDefaultTitle();
        videoFile = null;

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
            radioItem.setOnAction(actionEvent -> mediaPlayer.audio().setTrack(trackDescription.id()));
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
        disabledRadioItem.setOnAction(actionEvent -> disposeSubtitles());
        subtitlesMenu.getItems().add(disabledRadioItem);

        MenuItem subtitlesOpenItem = new MenuItem("Open .srt-file");
        subtitlesOpenItem.getStyleClass().add("italic");
        subtitlesOpenItem.setOnAction(actionEvent -> {
            embeddedMediaPlayer.controls().setPause(true);

            File file = fileChooserManager.chooseSubtitlesFile();
            if (file != null) {
                RadioMenuItem radioMenuItem = new RadioMenuItem(file.getName());
                radioMenuItem.setToggleGroup(subtitlesToggleGroup);
                radioMenuItem.setSelected(true);
                radioMenuItem.setOnAction(fileActionEvent -> initSubtitles(file.getAbsolutePath()));
                subtitlesMenu.getItems().add(subtitlesMenu.getItems().size() - 1, radioMenuItem);

                initSubtitles(file.getAbsolutePath());
            }

            embeddedMediaPlayer.controls().setPause(false);
        });
        subtitlesMenu.getItems().add(subtitlesOpenItem);

        // todo walk directory for subtitles
        /*for (TrackDescription trackDescription : mediaPlayer.subpictures().trackDescriptions()) { // todo delete
            System.out.println(trackDescription.toString());
        }*/

        subtitlesMenu.setDisable(false);
    }

    private void disposeSubtitlesMenu() {
        subtitlesMenu.setDisable(true);
        subtitlesMenu.getItems().clear();
        subtitlesToggleGroup.getToggles().clear();
    }

    private void initTimeLabels(MediaPlayer mediaPlayer) {
        Platform.runLater(() -> entireTimeLabel.setText(formatTime(mediaPlayer.status().length())));
    }

    private void disposeTimeLabels() {
        entireTimeLabel.setText(INITIAL_TIME);
        currentTimeLabel.setText(INITIAL_TIME);
    }

    private void initSubtitles(String fileName) {
        try (FileReader fileReader = new FileReader(fileName)) {
            Spus subtitleUnits = new BOMSrtParser().parse(fileReader);

            subtitlesHandler = new SpuHandler(subtitleUnits);
            //subtitlesHandler.setOffset(50);
            subtitlesHandler.addSpuEventListener(subtitleUnit -> {
                if (subtitleUnit != null) {
                    JavaFxSubtitles javaFxSubtitles = new JavaFxSubtitles(subtitleUnit.value().toString());
                    Platform.runLater(() -> javaFxSubtitles.updateTextFlow(textFlow));
                } else {
                    Platform.runLater(() -> textFlow.getChildren().clear());
                }
            });
        } catch (IOException|SpuParseException|RuntimeException e) { // TODO show message
            System.out.println("Could not find any subtitles in the provided file.");
            e.printStackTrace();
        }
    }

    private void disposeSubtitles() {
        subtitlesHandler = null;
        textFlow.getChildren().clear();
    }

    private void onExpandPressed() {
        sceneManager.toggleFullScreen();
        expandButton.getStyleClass().clear();
        expandButton.getStyleClass().add(
            sceneManager.isFullScreen() ? COLLAPSE_CLASSNAME : EXPAND_CLASSNAME);
    }

    private void onKeyReleased(KeyEvent keyEvent) {
        if (ON_EXPAND_KEYS.match(keyEvent) ||
            keyEvent.getCode() == ESCAPE && sceneManager.isFullScreen()
        ) {
            onExpandPressed();
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
