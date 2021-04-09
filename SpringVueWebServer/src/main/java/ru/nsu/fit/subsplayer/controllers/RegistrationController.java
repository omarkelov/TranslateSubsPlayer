package ru.nsu.fit.subsplayer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.entities.User;
import ru.nsu.fit.subsplayer.entities.UserRoles;
import ru.nsu.fit.subsplayer.repositories.UserRepository;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired private UserRepository userRepository;

    @GetMapping(Mappings.REGISTRATION)
    public String registration() {
        return "registration";
    }

    @PostMapping(Mappings.REGISTRATION)
    public String registerUser(User user, Map<String, Object> model) {
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.put("message", "User exists!");
            return "registration";
        }

        user.setActive(true);
        user.setUserRoles(Collections.singleton(UserRoles.USER));
        userRepository.save(user);

        return "redirect:" + Mappings.LOGIN;
    }
}
