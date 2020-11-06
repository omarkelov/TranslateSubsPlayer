package ru.nsu.fit.markelov.controllers.player;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ru.nsu.fit.markelov.javafxutil.MenuItemBuilder;
import ru.nsu.fit.markelov.managers.FileChooserManager;
import ru.nsu.fit.markelov.translation.iso639.ISO639;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static ru.nsu.fit.markelov.util.CharsetConverter.convertToUtf8;

public class MenuBarControl {

    private static final String DISABLED_SUBTITLES_MENU_ITEM_TEXT = "Disabled";
    private static final String OPEN_SUBTITLES_MENU_ITEM_TEXT = "Open .srt-file";

    private static final Comparator<MenuItem> MENU_ITEM_COMPARATOR
        = Comparator.comparing(MenuItem::getText);

    private final FileChooserManager fileChooserManager;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private final SubtitlesControl subtitlesControl;
    private final ControlBarControl controlBarControl;

    private final StackPane menuBarStackPane;
    private final HBox menuBarDaemonHBox;
    private final ToggleButton menuBarToggleButton;
    private final MenuBar menuBar;
    private final Menu audioMenu;
    private final Menu subtitlesMenu;
    private final Menu sourceLanguageMenu;
    private final Menu targetLanguageMenu;

    private final ToggleGroup audioToggleGroup = new ToggleGroup();
    private final ToggleGroup subtitlesToggleGroup = new ToggleGroup();
    private final ToggleGroup sourceLanguageToggleGroup = new ToggleGroup();
    private final ToggleGroup targetLanguageToggleGroup = new ToggleGroup();

    private File videoFile;

    public MenuBarControl(MenuBarObserver menuBarObserver, FileChooserManager fileChooserManager,
                          EmbeddedMediaPlayer embeddedMediaPlayer, SubtitlesControl subtitlesControl,
                          ControlBarControl controlBarControl, StackPane menuBarStackPane,
                          HBox menuBarDaemonHBox, ToggleButton menuBarToggleButton, MenuBar menuBar,
                          Menu audioMenu, Menu subtitlesMenu, Menu sourceLanguageMenu,
                          Menu targetLanguageMenu, MenuItem fileOpenItem, MenuItem fileCloseItem)
    {
        this.fileChooserManager = fileChooserManager;
        this.embeddedMediaPlayer = embeddedMediaPlayer;
        this.subtitlesControl = subtitlesControl;
        this.controlBarControl = controlBarControl;
        this.menuBarStackPane = menuBarStackPane;
        this.menuBarDaemonHBox = menuBarDaemonHBox;
        this.menuBarToggleButton = menuBarToggleButton;
        this.menuBar = menuBar;
        this.audioMenu = audioMenu;
        this.subtitlesMenu = subtitlesMenu;
        this.sourceLanguageMenu = sourceLanguageMenu;
        this.targetLanguageMenu = targetLanguageMenu;

        activateBindings();

        fileOpenItem.setOnAction(actionEvent -> menuBarObserver.onFileClicked());
        fileCloseItem.setOnAction(actionEvent -> menuBarObserver.onClosedClicked());

        initLanguageMenu(true, "English"); // todo -hardcode
        initLanguageMenu(false, "Russian"); // todo -hardcode
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

    public void fireMenuBarToggleButton() {
        menuBarToggleButton.fire();

        if (menuBarToggleButton.isSelected()) {
            menuBarStackPane.setVisible(true);
        } else {
            if (isAnyMenuShowing()) {
                return;
            }

            menuBarStackPane.setVisible(false);
        }
    }

    private void activateBindings() {
        menuBarStackPane.managedProperty().bind(menuBarStackPane.visibleProperty());

        menuBarDaemonHBox.visibleProperty().bind(menuBarStackPane.visibleProperty().not());
        menuBarDaemonHBox.managedProperty().bind(menuBarStackPane.managedProperty().not());
        menuBarDaemonHBox.prefHeightProperty().bind(menuBar.heightProperty());
        menuBarDaemonHBox.setOnMouseEntered(mouseEvent -> menuBarStackPane.setVisible(true));

        menuBar.disableProperty().bind(menuBarStackPane.visibleProperty().not());
        menuBar.setOnMouseExited(mouseEvent -> {
            if (menuBarToggleButton.isSelected()
                || mouseEvent.getPickResult().getIntersectedNode() == menuBarToggleButton
                || isAnyMenuShowing()
            ) {
                return;
            }

            menuBarStackPane.setVisible(false);
        });

        for (Menu menu : menuBar.getMenus()) {
            menu.setOnHidden(event -> {
                if (menuBarToggleButton.isSelected()) {
                    return;
                }

                Platform.runLater(() -> { // running later for giving the next menu some time to show up
                    if (isAnyMenuShowing()) {
                        return;
                    }

                    menuBarStackPane.setVisible(false);
                });
            });
        }
    }

    private boolean isAnyMenuShowing() {
        return menuBar.getMenus().stream().anyMatch(Menu::isShowing);
    }

    private void initAudioMenu() {
        for (TrackDescription trackDescription : embeddedMediaPlayer.audio().trackDescriptions()) {
            if (trackDescription.id() == -1) {
                continue;
            }

            RadioMenuItem radioItem = new MenuItemBuilder()
                .setText(convertToUtf8(trackDescription.description()))
                .setToggleGroup(audioToggleGroup)
                .setSelected(trackDescription.id() == embeddedMediaPlayer.audio().track())
                .setUserData(trackDescription.id())
                .buildRadio();
            radioItem.setOnAction(actionEvent -> embeddedMediaPlayer.audio().setTrack(trackDescription.id()));
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

        RadioMenuItem disabledRadioItem = new MenuItemBuilder()
            .setText(DISABLED_SUBTITLES_MENU_ITEM_TEXT)
            .setToggleGroup(subtitlesToggleGroup)
            .setSelected(true)
            .buildRadio();
        disabledRadioItem.setOnAction(actionEvent -> {
            subtitlesControl.disposeSubtitles();
            subtitlesControl.setCurrentSubtitlesMenuItem(disabledRadioItem);
        });
        subtitlesControl.setCurrentSubtitlesMenuItem(disabledRadioItem);
        subtitlesMenu.getItems().add(disabledRadioItem);

        MenuItem subtitlesOpenItem = new MenuItemBuilder()
            .setText(OPEN_SUBTITLES_MENU_ITEM_TEXT)
            .addStyleClass("italic")
            .buildStandard();
        subtitlesOpenItem.setOnAction(actionEvent -> {
            boolean isPlaying = embeddedMediaPlayer.status().isPlaying();
            if (isPlaying) {
                controlBarControl.onPausePressed(true);
            }

            File file = fileChooserManager.chooseSubtitlesFile();
            if (file != null) {
                RadioMenuItem radioMenuItem = new MenuItemBuilder()
                    .setText(file.getName())
                    .setToggleGroup(subtitlesToggleGroup)
                    .setSelected(true)
                    .buildRadio();
                radioMenuItem.setOnAction(fileActionEvent ->
                    subtitlesControl.initSubtitles(file.getAbsolutePath(), radioMenuItem, (long) controlBarControl.getSliderValue()));
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
                    boolean disabled = ((RadioMenuItem) subtitlesToggleGroup.getSelectedToggle())
                        .getText().equals(DISABLED_SUBTITLES_MENU_ITEM_TEXT);

                    RadioMenuItem radioMenuItem = new MenuItemBuilder()
                        .setText(videoFilePath.relativize(subtitlesPath).toString())
                        .setToggleGroup(subtitlesToggleGroup)
                        .setSelected(disabled)
                        .buildRadio();
                    radioMenuItem.setOnAction(fileActionEvent -> subtitlesControl.initSubtitles(
                        subtitlesPath.toString(), radioMenuItem, (long) controlBarControl.getSliderValue()));
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

    private void initLanguageMenu(boolean isSource, String defaultLanguage) {
        Menu menu = isSource ? sourceLanguageMenu : targetLanguageMenu;
        ToggleGroup toggleGroup = isSource ? sourceLanguageToggleGroup : targetLanguageToggleGroup;

        for (String language : ISO639.getLanguages()) {
            String languageCode = ISO639.getLanguageCode(language);

            RadioMenuItem languageRadioItem = new MenuItemBuilder()
                .setText(language)
                .setToggleGroup(toggleGroup)
                .buildRadio();
            languageRadioItem.setOnAction(actionEvent -> {
                if (isSource) {
                    subtitlesControl.setSourceLanguageCode(languageCode);
                } else {
                    subtitlesControl.setTargetLanguageCode(languageCode);
                }

                menu.getItems().remove(languageRadioItem);
                menu.getItems().sort(MENU_ITEM_COMPARATOR);
                menu.getItems().add(0, languageRadioItem);
            });

            if (language.equals(defaultLanguage)) {
                if (isSource) {
                    subtitlesControl.setSourceLanguageCode(languageCode);
                } else {
                    subtitlesControl.setTargetLanguageCode(languageCode);
                }

                languageRadioItem.setSelected(true);
                menu.getItems().add(0, languageRadioItem);
            } else {
                menu.getItems().add(languageRadioItem);
            }
        }
    }
}
