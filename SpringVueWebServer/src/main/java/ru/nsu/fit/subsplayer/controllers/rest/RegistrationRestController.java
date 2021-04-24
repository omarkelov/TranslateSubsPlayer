package ru.nsu.fit.subsplayer.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.User;
import ru.nsu.fit.subsplayer.database.entities.UserRoles;
import ru.nsu.fit.subsplayer.database.repositories.UserRepository;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class RegistrationRestController {

    @Autowired private UserRepository userRepository;

    @PostMapping(Mappings.REGISTRATION)
    public String registerUser(User user, Map<String, Object> model) { // todo
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
