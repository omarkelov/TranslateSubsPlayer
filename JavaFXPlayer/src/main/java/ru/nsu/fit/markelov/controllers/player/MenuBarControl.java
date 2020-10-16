package ru.nsu.fit.markelov.controllers.player;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuBarControl {

    private final FileChooserManager fileChooserManager;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private final SubtitlesControl subtitlesControl;
    private final ControlBarControl controlBarControl;

    private final Menu audioMenu;
    private final Menu subtitlesMenu;

    private final ToggleGroup audioToggleGroup = new ToggleGroup();
    private final ToggleGroup subtitlesToggleGroup = new ToggleGroup();

    private File videoFile;

    public MenuBarControl(MenuBarObserver menuBarObserver, FileChooserManager fileChooserManager,
                          EmbeddedMediaPlayer embeddedMediaPlayer, SubtitlesControl subtitlesControl,
                          ControlBarControl controlBarControl, Menu audioMenu, Menu subtitlesMenu,
                          MenuItem fileOpenItem, MenuItem fileCloseItem) {
        this.fileChooserManager = fileChooserManager;
        this.embeddedMediaPlayer = embeddedMediaPlayer;
        this.subtitlesControl = subtitlesControl;
        this.controlBarControl = controlBarControl;
        this.audioMenu = audioMenu;
        this.subtitlesMenu = subtitlesMenu;

        fileOpenItem.setOnAction(actionEvent -> menuBarObserver.onFileClicked());
        fileCloseItem.setOnAction(actionEvent -> menuBarObserver.onClosedClicked());
    }

    public void init() {
        initAudioMenu();
        initSubtitlesMenu();
    }

    public void dispose() {
        disposeAudioMenu();
        disposeSubtitlesMenu();
    }

    public int getSelectedAudioTrack() {
        return (Integer) audioToggleGroup.getSelectedToggle().getUserData();
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    private void initAudioMenu() {
        for (TrackDescription trackDescription : embeddedMediaPlayer.audio().trackDescriptions()) {
            if (trackDescription.id() == -1) {
                continue;
            }

            RadioMenuItem radioItem = new RadioMenuItem(trackDescription.description());
            radioItem.setToggleGroup(audioToggleGroup);
            radioItem.setSelected(trackDescription.id() == embeddedMediaPlayer.audio().track());
            radioItem.setUserData(trackDescription.id());
            radioItem.setOnAction(actionEvent -> embeddedMediaPlayer.audio().setTrack(trackDescription.id()));
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

    private void initSubtitlesMenu() {
        embeddedMediaPlayer.subpictures().setTrack(-1); // disable subtitles inside vlc-player

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
}
