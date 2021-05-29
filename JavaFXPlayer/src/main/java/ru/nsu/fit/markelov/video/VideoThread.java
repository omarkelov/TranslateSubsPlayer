package ru.nsu.fit.markelov.video;

import ru.nsu.fit.markelov.user.UserManager;

import java.io.File;
import java.util.List;

public class VideoThread extends Thread {

    private static final long SLEEP_TIME = 1000;

    private final UserManager userManager;

    public VideoThread(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(SLEEP_TIME);

                List<ContextVideoInfo> contextVideoInfoList = userManager.getContextVideoInfoList();
                if (contextVideoInfoList == null) {
                    continue;
                }

                for (ContextVideoInfo contextVideoInfo : contextVideoInfoList) {
                    if (Thread.interrupted()) {
                        return;
                    }

                    File contextVideoFile = VideoEditor.cutVideo(contextVideoInfo);
                    if (contextVideoFile == null) {
                        continue;
                    }

                    userManager.uploadVideo(contextVideoInfo.getContextId(), contextVideoFile);

                    contextVideoFile.delete();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
