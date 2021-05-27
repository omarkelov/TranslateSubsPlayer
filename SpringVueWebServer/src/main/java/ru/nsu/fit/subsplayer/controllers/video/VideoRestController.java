package ru.nsu.fit.subsplayer.controllers.video;

import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.fit.subsplayer.constants.Mappings;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.services.AccessoryService;
import ru.nsu.fit.subsplayer.services.FileStorageService;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public class VideoRestController {

    @Autowired private AccessoryService accessoryService;
    @Autowired private ContextRepository contextRepository;
    @Autowired private FileStorageService fileStorageService;

    @PostMapping(Mappings.VIDEO)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadVideo(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam long contextId,
                            @RequestParam MultipartFile file) {

        accessoryService.checkContextAccess(userDetails, contextId);

        Optional<Context> optionalContext = contextRepository.findById(contextId);
        if (optionalContext.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such context");
        }

        Context context = optionalContext.get();
        if (context.getLink() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Context already has a link");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No filename provided");
        }

        fileName = UUID.randomUUID() + "." + Files.getFileExtension(fileName);

        fileStorageService.storeFile(fileName, file);

        context.setLink(Mappings.VIDEO + "/" + fileName);
        contextRepository.save(context);
    }
}
