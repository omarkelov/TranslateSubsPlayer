package ru.nsu.fit.subsplayer.controllers.video;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Component
public class VideoResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    public final static String ATTR_FILE = VideoResourceHttpRequestHandler.class.getName() + ".file";

    @Override
    protected Resource getResource(HttpServletRequest request) {
        return new FileSystemResource((File) request.getAttribute(ATTR_FILE));
    }
}
