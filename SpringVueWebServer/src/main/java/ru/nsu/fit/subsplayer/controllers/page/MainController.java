package ru.nsu.fit.subsplayer.controllers.page;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.nsu.fit.subsplayer.constants.Mappings;

@Controller
@RequestMapping(value = "/", produces = "text/html")
public class MainController {

    @GetMapping("/")
    public RedirectView main(@AuthenticationPrincipal UserDetails userDetails) {
        return new RedirectView(userDetails == null ? Mappings.LOGIN : Mappings.MOVIES);
    }

    @GetMapping("/errors/notFound")
    public RedirectView errorNotFound() {
        return new RedirectView("/");
    }

    @GetMapping(Mappings.LOGIN)
    public String login() {
        return "index";
    }

    @GetMapping(Mappings.REGISTRATION)
    public String registration() {
        return "index";
    }
}
