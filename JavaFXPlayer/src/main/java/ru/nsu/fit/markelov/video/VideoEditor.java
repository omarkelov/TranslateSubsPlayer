package ru.nsu.fit.markelov.video;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class VideoEditor {

    private static final boolean IS_WINDOWS =
        System.getProperty("os.name").toLowerCase().startsWith("windows");

    private static final File FFMPEG_DIRECTORY = new File("C:\\ffmpeg\\bin"); // todo -hardcode

    public static File cutVideo(ContextVideoInfo contextVideoInfo) {
        try {
            String newVideoFileName = UUID.randomUUID() + ".mp4";

            String command = String.format(
                "ffmpeg -ss %dms -i \"%s\" -to %dms -c copy -map 0:v:0 -map 0:a:%d %s",
                contextVideoInfo.getStartTime(),
                contextVideoInfo.getVideoFilePath(),
                contextVideoInfo.getEndTime() - contextVideoInfo.getStartTime(),
                contextVideoInfo.getAudioChannel(),
                newVideoFileName);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(FFMPEG_DIRECTORY);

            if (IS_WINDOWS) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            process.destroy();
            if (exitCode != 0) {
                System.err.println("Bad exitCode: " + exitCode);
                return null;
            }

            return new File(FFMPEG_DIRECTORY + "\\" + newVideoFileName);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
