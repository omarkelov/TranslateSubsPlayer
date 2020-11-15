### Movies
| UUID | Movie               |
|------|---------------------|
| 1    | Breaking Bad s01e01 |
| 2    | Rêver Chiot         |
| 3    | Breaking Bad s01e02 |
| 4    | House M.D. s01e20   |

### Contexts
| UUID | MovieId | Context                                                                                                                      | Link         |
|------|---------|------------------------------------------------------------------------------------------------------------------------------|--------------|
| 1    | 2       | Lorem superposés valise pourparlers rêver chiots rendez\-vous naissance Eiffel myrtille\.                                    | videos/1.mp4 |
| 2    | 2       | Nous avoir parole la nous moussant\.                                                                                         | videos/2.mp4 |
| 3    | 2       | Bourguignon penser câlin millésime peripherique annoncer enfants enfants vachement nuit formidable encombré épanoui chiots\. | videos/3.mp4 |

### Phrases
| UUID | ContextId | Phrase         | CorrectedPhrase | Type     | Translation    | Priority | SuccessfulAttempts | Attempts |
|------|-----------|----------------|-----------------|----------|----------------|----------|--------------------|----------|
| 1    | 1         | rêver chiots   | rêver chiot     | phrase   | dreaming puppy | 0        | 0                  | 2        |
| 2    | 2         | parole la nous | NULL            | phraseme | talk to us     | 0        | 0                  | 0        |
| 3    | 3         | penser         | NULL            | word     | think          | 1        | 1                  | 1        |
| 4    | 3         | nuit           | NULL            | word     | night          | 0        | 2                  | 3        |
