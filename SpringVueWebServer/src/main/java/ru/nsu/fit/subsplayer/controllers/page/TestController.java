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
import ru.nsu.fit.subsplayer.controllers.rest.TestRestController;

import java.util.Map;

@Controller
@RequestMapping(value = "/", produces = "text/html")
public class TestController {

    @Autowired TestRestController testRestController;

    @GetMapping(Mappings.MOVIES + "/{movieName}/test")
    public String getTest(Map<String, Object> model,
                          @AuthenticationPrincipal UserDetails userDetails,
                          @PathVariable String movieName) {

        try {
            model.put("username", userDetails.getUsername());
            model.put("test", testRestController.getTest(userDetails, movieName));
        } catch (ResponseStatusException e) {
            return "redirect:/";
        }

        return "index";
    }
}
