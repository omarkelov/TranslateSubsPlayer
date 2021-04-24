package ru.nsu.fit.subsplayer.controllers.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.controllers.rest.RawMovieRestController;

import java.util.Map;

@Controller
@RequestMapping(value = "/", produces = "text/html")
public class RawMovieController {

    @Autowired RawMovieRestController rawMovieRestController;

    @GetMapping(Mappings.RAW_MOVIES)
    public String getMovies(Map<String, Object> model,
                            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            model.put("username", userDetails.getUsername());
            model.put("rawMovies", rawMovieRestController.getMovies(userDetails));
        } catch (ResponseStatusException e) {
            return "redirect:/";
        }

        return "index";
    }

    @GetMapping(Mappings.RAW_MOVIES + "/{rawMovieId}")
    public String getRawMovie(Map<String, Object> model,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable Long rawMovieId) {

        try {
            model.put("username", userDetails.getUsername());
            model.put("rawMovie", rawMovieRestController.getRawMovie(userDetails, rawMovieId));
        } catch (ResponseStatusException e) {
            return "redirect:/";
        }

        return "index";
    }
}
