package ru.nsu.fit.markelov.managers;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

import java.io.File;
import java.util.prefs.Preferences;

import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;

/**
 * FileChooserManager class is used for letting user to choose files from the open dialogs.
 *
 * @author Oleg Markelov
 */
public class FileChooserManager {

    private static final String LAST_VIDEO_DIRECTORY_PREFERENCES = "LastVideoDirectory";

    private final Window window;

    private final FileChooser videoFileChooser;
    private final FileChooser subtitlesFileChooser;

    private final Preferences preferences;

    /**
     * Creates new FileChooserManager with specified JavaFX window.
     *
     * @param window JavaFX window.
     * @throws IllegalInputException if one of the input parameters is null.
     */
    public FileChooserManager(Window window) throws IllegalInputException {
        this.window = requireNonNull(window);

        videoFileChooser = new FileChooser();
        videoFileChooser.setTitle("Choose Video");

        subtitlesFileChooser = new FileChooser();
        subtitlesFileChooser.setTitle("Choose Subtitles");
        subtitlesFileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("SRT", "*.srt"));

        preferences = Preferences.userRoot().node(getClass().getName());
    }

    /**
     * Lets user to choose a video file.
     *
     * @return chosen video file.
     */
    public File chooseVideoFile() { // TODO only video
        videoFileChooser.setInitialDirectory(new File(
            preferences.get(LAST_VIDEO_DIRECTORY_PREFERENCES, System.getProperty("user.home"))));

        File file = videoFileChooser.showOpenDialog(window);
        if (file != null) {
            preferences.put(LAST_VIDEO_DIRECTORY_PREFERENCES, file.getParent());
        }

        return file;
    }

    /**
     * Lets user to choose a subtitles file.
     *
     * @return chosen subtitles file.
     */
    public File chooseSubtitlesFile() {
        subtitlesFileChooser.setInitialDirectory(new File(
            preferences.get(LAST_VIDEO_DIRECTORY_PREFERENCES, System.getProperty("user.home"))));

        return subtitlesFileChooser.showOpenDialog(window);
    }
}
