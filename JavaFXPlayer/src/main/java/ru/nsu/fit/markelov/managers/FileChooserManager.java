package ru.nsu.fit.markelov.managers;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import ru.nsu.fit.markelov.util.validation.IllegalInputException;

import java.io.File;

import static ru.nsu.fit.markelov.util.validation.IllegalInputException.requireNonNull;

/**
 * FileChooserManager class is used for letting user to choose files from the open dialogs.
 *
 * @author Oleg Markelov
 */
public class FileChooserManager {

    private final Window window;

    private final FileChooser videoFileChooser;
    private final FileChooser subtitlesFileChooser;

    private File lastVideoDirectory;

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

        lastVideoDirectory = new File("F:\\Multimedia\\Series\\Westworld\\1\\"); // TODO -hardcode
    }

    /**
     * Lets user to choose a video file.
     *
     * @return chosen video file.
     */
    public File chooseVideoFile() { // TODO only video
        validateLastVideoDirectory();
        videoFileChooser.setInitialDirectory(lastVideoDirectory);

        File file = videoFileChooser.showOpenDialog(window);
        if (file != null) {
            lastVideoDirectory = new File(file.getParent());
        }

        return file;
    }

    /**
     * Lets user to choose a subtitles file.
     *
     * @return chosen subtitles file.
     */
    public File chooseSubtitlesFile() {
        validateLastVideoDirectory();
        subtitlesFileChooser.setInitialDirectory(lastVideoDirectory);

        return subtitlesFileChooser.showOpenDialog(window);
    }

    private void validateLastVideoDirectory() {
        if (!lastVideoDirectory.exists()) {
            lastVideoDirectory = new File(System.getProperty("user.home"));
        }
    }
}
