package ru.nsu.fit.markelov.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.subtitles.BOMSrtParser;
import ru.nsu.fit.markelov.subtitles.JavaFxSubtitles;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.subs.Spus;
import uk.co.caprica.vlcj.subs.handler.SpuHandler;
import uk.co.caprica.vlcj.subs.parser.SpuParseException;

import java.io.FileReader;
import java.io.IOException;

import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 * PlayerController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a player scene.
 *
 * @author Oleg Markelov
 */
public class PlayerController implements Controller {

    private static final String FXML_FILE_NAME = "player.fxml";

    @FXML private StackPane root;
    @FXML private ImageView videoImageView;
    @FXML private GridPane gridPane;
    @FXML private TextFlow textFlow;

    private final SceneManager sceneManager;

    private SpuHandler spuHandler;
    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;

    /**
     * Creates new PlayerController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     * @throws IllegalInputException if one of the input parameters is null.
     */
    public PlayerController(SceneManager sceneManager) throws IllegalInputException {
        this.sceneManager = requireNonNull(sceneManager);

        mediaPlayerFactory = new MediaPlayerFactory();
        embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

        Spus spus;
        try (FileReader fileReader = new FileReader("F:\\Multimedia\\Series\\Westworld\\1\\Subtitles\\Westworld.[S01E01].HD720.DUB.[qqss44].eng.srt")) {
            spus = new BOMSrtParser().parse(fileReader);
        } catch (IOException|SpuParseException|RuntimeException e) { // TODO
            e.printStackTrace();
            spus = null;
        }

        if (spus != null) {
            spuHandler = new SpuHandler(spus);
//            spuHandler.setOffset(50);
            spuHandler.addSpuEventListener(spu -> {
                if (spu != null) {
                    JavaFxSubtitles javaFxSubtitles = new JavaFxSubtitles(spu.value().toString());
                    Platform.runLater(() -> javaFxSubtitles.updateTextFlow(textFlow));
                } else {
                    Platform.runLater(() -> textFlow.getChildren().clear());
                }
            });
        } else {
            System.out.println("Could not find any subtitles in the provided file.");
        }

        embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                mediaPlayer.submit(() -> {
                    mediaPlayer.audio().setTrack(2);
                    mediaPlayer.subpictures().setTrack(-1);
//                    mediaPlayer.controls().setTime(3 * 60 * 1000 + 20 * 1000);
                    mediaPlayer.controls().setTime(2 * 60 * 1000 + 47 * 1000);
//                    mediaPlayer.controls().setPosition(0.3f);

                    /*for (TrackDescription trackDescription : mediaPlayer.audio().trackDescriptions()) {
                        System.out.println(trackDescription.toString());
                    }*/
                });
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (spuHandler != null) {
                    spuHandler.setTime(newTime);
                }

                /*if (newTime > 2 * 60 * 1000 + 0 * 1000) {
                    mediaPlayer.submit(() -> mediaPlayer.controls().setTime(1 * 60 * 1000 + 47 * 1000));
                }*/
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("----- ERROR -----");
            }
        });
    }

    @FXML
    private void initialize() {
        embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(videoImageView));

        videoImageView.fitWidthProperty().bind(root.widthProperty());
        videoImageView.fitHeightProperty().bind(root.heightProperty());

        embeddedMediaPlayer.media().play("F:\\Multimedia\\Series\\Westworld\\1\\Westworld.[S01E01].HD720.DUB.[qqss44].mkv");
//        embeddedMediaPlayer.media().play("F:\\Multimedia\\Films\\Collection\\Death.Proof.2007.BluRay.1080p.DTS.x264.dxva-EuReKA.mkv");
//        embeddedMediaPlayer.media().play("F:\\Multimedia\\Films\\Collection\\Baby.Driver.2017.BDRip.1.46Gb.Dub.MegaPeer.avi");
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
    public void close() {
        embeddedMediaPlayer.controls().stop();
        embeddedMediaPlayer.release();
        mediaPlayerFactory.release();
    }
}
