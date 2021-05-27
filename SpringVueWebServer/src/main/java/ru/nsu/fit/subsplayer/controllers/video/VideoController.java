package ru.nsu.fit.subsplayer.controllers.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.constants.Locations;
import ru.nsu.fit.subsplayer.constants.Mappings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
public class VideoController {

    @Autowired private VideoResourceHttpRequestHandler videoResourceHttpRequestHandler;

    @GetMapping(Mappings.VIDEO + "/{videoFileName}")
    public void getVideo(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable String videoFileName) {

        try {
            request.setAttribute(VideoResourceHttpRequestHandler.ATTR_FILE,
                new File(Locations.VIDEO + videoFileName));
            videoResourceHttpRequestHandler.handleRequest(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No video: " + videoFileName);
        }
    }
}
