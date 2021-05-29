package ru.nsu.fit.subsplayer.controllers.video;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.ContextVideoInfo;
import ru.nsu.fit.subsplayer.database.entities.Movie;
import ru.nsu.fit.subsplayer.database.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.database.repositories.MovieRepository;
import ru.nsu.fit.subsplayer.database.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class ContextVideoInfoRestController {

    @Autowired private UserRepository userRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ContextRepository contextRepository;

    @GetMapping(Mappings.CONTEXT_VIDEO_INFO)
    public String getContextVideoInfo(@AuthenticationPrincipal UserDetails userDetails) {
        long userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        List<Context> contexts = contextRepository.findWithNoLink(userId);

        List<ContextVideoInfo> contextVideoInfoList = new ArrayList<>();
        for (Context context : contexts) {
            Movie movie = movieRepository.findByContextId(context.getId());
            contextVideoInfoList.add(new ContextVideoInfo(context, movie));
        }

        return new Gson().toJson(contextVideoInfoList);
    }
}
