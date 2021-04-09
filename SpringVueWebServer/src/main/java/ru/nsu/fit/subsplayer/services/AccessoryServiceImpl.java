package ru.nsu.fit.subsplayer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.entities.User;
import ru.nsu.fit.subsplayer.repositories.UserRepository;

@Component
public class AccessoryServiceImpl implements AccessoryService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void checkPhraseAccess(UserDetails userDetails, long phraseId) {
        User user = userRepository.findByPhraseId(phraseId); // todo move this part to new class
        if (user == null || !user.getUsername().equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Phrase (id " + phraseId + ") not found");
        }
    }
}
