package ru.nsu.fit.subsplayer.__dev__;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(Map<String, Object> model) {
        return "main";
    }

    @GetMapping("/index")
    public String index(Map<String, Object> model, @AuthenticationPrincipal UserDetails user) {
        return "index";
    }
}
