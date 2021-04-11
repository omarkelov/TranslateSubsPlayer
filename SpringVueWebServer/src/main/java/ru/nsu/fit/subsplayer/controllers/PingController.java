package ru.nsu.fit.subsplayer.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class PingController {

    @GetMapping(Mappings.PING)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ping(@AuthenticationPrincipal UserDetails userDetails) {}
}
