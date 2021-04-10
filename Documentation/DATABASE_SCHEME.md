### users
| id | username | password |
|----|----------|----------|
| 1  | admin    | ******** |
| 2  | Jonh_Doe | ******** |

### movies
| id | user_id | name                |
|----|---------|---------------------|
| 1  | 2       | Breaking Bad s01e01 |
| 2  | 2       | Rêver Chiot         |
| 3  | 2       | Breaking Bad s01e02 |
| 4  | 2       | House M.D. s01e20   |

### contexts
| id | movie_id | context                                                                                                                      | link         |
|----|----------|------------------------------------------------------------------------------------------------------------------------------|--------------|
| 1  | 2        | Lorem superposés valise pourparlers rêver chiots rendez\-vous naissance Eiffel myrtille\.                                    | videos/1.mp4 |
| 2  | 2        | Nous avoir parole la nous moussant\.                                                                                         | videos/2.mp4 |
| 3  | 2        | Bourguignon penser câlin millésime peripherique annoncer enfants enfants vachement nuit formidable encombré épanoui chiots\. | videos/3.mp4 |

### phrases
| id | context_id | phrase         | corrected_phrase | type     | translation    |
|----|------------|----------------|------------------|----------|----------------|
| 1  | 1          | rêver chiots   | rêver chiot      | phrase   | dreaming puppy |
| 2  | 2          | parole la nous | NULL             | phraseme | talk to us     |
| 3  | 3          | penser         | NULL             | word     | think          |
| 4  | 3          | nuit           | NULL             | word     | night          |

### phrase_stats
| id | phrase_id | priority | successful_attempts | attempts |
|----|-----------|----------|---------------------|----------|
| 1  | 1         | 0        | 0                   | 2        |
| 2  | 2         | 0        | 0                   | 0        |
| 3  | 3         | 1        | 1                   | 1        |
| 4  | 4         | 0        | 2                   | 3        |
