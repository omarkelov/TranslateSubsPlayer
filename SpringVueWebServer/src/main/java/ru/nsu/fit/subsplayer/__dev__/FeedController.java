package ru.nsu.fit.subsplayer.__dev__;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.nsu.fit.subsplayer.entities.Context;
import ru.nsu.fit.subsplayer.entities.Movie;
import ru.nsu.fit.subsplayer.entities.Phrase;
import ru.nsu.fit.subsplayer.entities.PhraseStats;
import ru.nsu.fit.subsplayer.entities.User;
import ru.nsu.fit.subsplayer.entities.UserRoles;
import ru.nsu.fit.subsplayer.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.repositories.MovieRepository;
import ru.nsu.fit.subsplayer.repositories.PhraseRepository;
import ru.nsu.fit.subsplayer.repositories.PhraseStatsRepository;
import ru.nsu.fit.subsplayer.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Controller
public class FeedController {

    @Autowired private UserRepository userRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private ContextRepository contextRepository;
    @Autowired private PhraseRepository phraseRepository;
    @Autowired private PhraseStatsRepository phraseStatsRepository;

    @PostConstruct
    private void feed() {
        if (userRepository.findByUsername("admin") == null) {
            final long finalUserId = userRepository.save(new User(
                "admin", "admin", true, Collections.singleton(UserRoles.USER))).getId();

            feedWithMovies(new ArrayList<>() {{
                add(new Movie(
                    finalUserId,
                    "Shrek",
                    new ArrayList<>() {{
                        add(new Context(
                            "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort...",
                            "videos/0.mp4",
                            new ArrayList<>() {{
                                add(new Phrase(
                                    "Once upon a time",
                                    null,
                                    null,
                                    "Давным-давно"
                                ));
                                add(new Phrase(
                                    "sort",
                                    null,
                                    "имя существительное",
                                    "сорт | род"
                                ));
                            }}
                        ));

                        add(new Context(
                            "She was locked away in a castle... guarded by a terrible fire-breathing dragon.",
                            "videos/1.mp4",
                            new ArrayList<>() {{
                                add(new Phrase(
                                    "castle",
                                    "a castle",
                                    "имя существительное",
                                    "замок | дворец"
                                ));
                            }}
                        ));

                        add(new Context(
                            "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort... which could only be broken by love's first kiss. She was locked away in a castle... guarded by a terrible fire-breathing dragon. Many brave knights had attempted to free her from this deadful prison. But none prevailed. She waited in the dragon's keep... in the highest room of the tallest tower... for her true love and true love's first kiss. Like that's ever gonna happen.",
                            "videos/2.mp4",
                            new ArrayList<>() {{
                                add(new Phrase(
                                    "Once upon a time",
                                    null,
                                    null,
                                    "Давным-давно"
                                ));
                                add(new Phrase(
                                    "sort",
                                    null,
                                    "имя существительное",
                                    "сорт | род"
                                ));
                                add(new Phrase(
                                    "castle",
                                    "a castle",
                                    "имя существительное",
                                    "замок | дворец"
                                ));
                            }}
                        ));
                    }}
                ));

                add(new Movie(
                    finalUserId,
                    "Shrek 2",
                    new ArrayList<>() {{
                        add(new Context(
                            "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort...",
                            "videos/3.mp4",
                            new ArrayList<>() {{
                                add(new Phrase(
                                    "Once upon a time",
                                    null,
                                    null,
                                    "Давным-давно"
                                ));
                                add(new Phrase(
                                    "sort",
                                    null,
                                    "имя существительное",
                                    "сорт | род"
                                ));
                            }}
                        ));

                        add(new Context(
                            "She was locked away in a castle... guarded by a terrible fire-breathing dragon.",
                            "videos/4.mp4",
                            new ArrayList<>() {{
                                add(new Phrase(
                                    "castle",
                                    "a castle",
                                    "имя существительное",
                                    "замок | дворец"
                                ));
                            }}
                        ));

                        add(new Context(
                            "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort... which could only be broken by love's first kiss. She was locked away in a castle... guarded by a terrible fire-breathing dragon. Many brave knights had attempted to free her from this deadful prison. But none prevailed. She waited in the dragon's keep... in the highest room of the tallest tower... for her true love and true love's first kiss. Like that's ever gonna happen.",
                            "videos/5.mp4",
                            new ArrayList<>() {{
                                add(new Phrase(
                                    "Once upon a time",
                                    null,
                                    null,
                                    "Давным-давно"
                                ));
                                add(new Phrase(
                                    "sort",
                                    null,
                                    "имя существительное",
                                    "сорт | род"
                                ));
                                add(new Phrase(
                                    "castle",
                                    "a castle",
                                    "имя существительное",
                                    "замок | дворец"
                                ));
                            }}
                        ));
                    }}
                ));
            }});
        }
    }

    private void feedWithMovies(Collection<Movie> movies) {
        for (Movie movie : movies) {
            long movieId = movieRepository.save(movie).getId();
            for (Context context : movie.getContexts()) {
                context.setMovieId(movieId);
                feedWithContext(context);
            }
        }
    }

    private void feedWithContext(Context context) {
        long contextId = contextRepository.save(context).getId();

        for (Phrase phrase : context.getPhrases()) {
            phrase.setContextId(contextId);
            phrase = phraseRepository.save(phrase);
            phraseStatsRepository.save(new PhraseStats(phrase.getId()));
        }
    }
}
