package ru.nsu.fit.subsplayer.__dev__;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.nsu.fit.subsplayer.database.entities.Context;
import ru.nsu.fit.subsplayer.database.entities.Movie;
import ru.nsu.fit.subsplayer.database.entities.Phrase;
import ru.nsu.fit.subsplayer.database.entities.PhraseStats;
import ru.nsu.fit.subsplayer.database.entities.RawMovie;
import ru.nsu.fit.subsplayer.database.entities.RawPhrase;
import ru.nsu.fit.subsplayer.database.entities.User;
import ru.nsu.fit.subsplayer.database.entities.UserRoles;
import ru.nsu.fit.subsplayer.database.repositories.ContextRepository;
import ru.nsu.fit.subsplayer.database.repositories.MovieRepository;
import ru.nsu.fit.subsplayer.database.repositories.PhraseRepository;
import ru.nsu.fit.subsplayer.database.repositories.PhraseStatsRepository;
import ru.nsu.fit.subsplayer.database.repositories.RawMovieRepository;
import ru.nsu.fit.subsplayer.database.repositories.RawPhraseRepository;
import ru.nsu.fit.subsplayer.database.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Controller
public class FeedController {

    @Autowired private UserRepository userRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private RawMovieRepository rawMovieRepository;
    @Autowired private RawPhraseRepository rawPhraseRepository;
    @Autowired private ContextRepository contextRepository;
    @Autowired private PhraseRepository phraseRepository;
    @Autowired private PhraseStatsRepository phraseStatsRepository;

    @PostConstruct
    private void feed() {
        if (userRepository.findByUsername("admin") == null) {
            final long userId = userRepository.save(new User(
                "admin", "admin", true, Collections.singleton(UserRoles.USER))).getId();

            feedWithRawMovies(new ArrayList<>() {{
                add(new RawMovie(
                    userId,
                    "1",
                    "C:/Movies/Shrek",
                    "[{\"start\":47580,\"end\":52450,\"text\":\"Once upon a time there was a lovely princess\"},{\"start\":52520,\"end\":56290,\"text\":\"But she had an enchantment upon her of a fearful sort...\"},{\"start\":56350,\"end\":61220,\"text\":\"which could only be broken by love's first kiss.\"},{\"start\":61320,\"end\":63660,\"text\":\"She was locked away in a castle...\"},{\"start\":63760,\"end\":67960,\"text\":\"guarded by a terrible fire-breathing dragon.\"},{\"start\":68060,\"end\":72400,\"text\":\"Many brave knights had attempted to free her from this deadful prison.\"},{\"start\":72470,\"end\":75270,\"text\":\"but none prevailed.\"},{\"start\":75370,\"end\":77510,\"text\":\"She waited in the dragon's keep...\"},{\"start\":77570,\"end\":80510,\"text\":\"in the highest room of the tallest tower...\"},{\"start\":80580,\"end\":85150,\"text\":\"for her true love and true love's first kiss.\"},{\"start\":75210,\"end\":88180,\"text\":\"Like that's ever gonna happen\"}]",
                    new ArrayList<>() {{
                        add(new RawPhrase("{\"lineId\":0,\"phrase\":\"Once upon a time\",\"translation\":{\"main\":\"Давным-давно\"}}", true));
                        add(new RawPhrase("{\"lineId\":1,\"phrase\":\"sort\",\"translation\":{\"main\":\"Сортировать\",\"groups\":[{\"partOfSpeech\":\"глагол\",\"variants\":[\"сортировать\",\"классифицировать\",\"разбирать\"]},{\"partOfSpeech\":\"имя существительное\",\"variants\":[\"сорт\",\"род\",\"разновидность\",\"образ\",\"разряд\",\"манера\",\"литеры\",\"характер\",\"способ\",\"качество\"]}]}}", null));
                        add(new RawPhrase("{\"lineId\":3,\"phrase\":\"castle\",\"translation\":{\"main\":\"замок\",\"groups\":[{\"partOfSpeech\":\"имя существительное\",\"variants\":[\"замок\",\"дворец\",\"ладья\",\"твердыня\",\"рокировка\",\"убежище\"]},{\"partOfSpeech\":\"глагол\",\"variants\":[\"рокировать\",\"рокироваться\"]}]}}", null));
                    }}
                ));

                add(new RawMovie(
                    userId,
                    "2",
                    "C:/Movies/Shrek 2",
                    "[]",
                    new ArrayList<>()
                ));
            }});

            feedWithMovies(new ArrayList<>() {{
                add(new Movie(
                    userId,
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
                    userId,
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

    private void feedWithRawMovies(Collection<RawMovie> rawMovies) {
        for (RawMovie rawMovie : rawMovies) {
            long rawMovieId = rawMovieRepository.save(rawMovie).getId();
            for (RawPhrase rawPhrase : rawMovie.getPhrases()) {
                rawPhrase.setRawMovieId(rawMovieId);
                rawPhraseRepository.save(rawPhrase);
            }
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
