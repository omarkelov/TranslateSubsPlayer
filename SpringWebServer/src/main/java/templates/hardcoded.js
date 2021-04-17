var contextId = 0;

function getNextTest() {
    let context = contexts[contextId];

    if (contextId < contexts.length - 1) {
        contextId++;
    } else {
        contextId = 0;
    }

    return context;
}

function sendResult(movieId, contextId, phraseId, correct) {
    console.log(movieId + " ||| " + contextId + " ||| " + phraseId + " ||| " + correct);
}

var contexts = [
    {
        "phraseNumber": 0,
        "context": {
            "id": 0,
            "movieId": 0,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort...",
            "link": "0.mp4",
            "phrases": [
                {
                    "phraseId": 0,
                    "phrase": "Once upon a time",
                    "correctedPhrase": null,
                    "type": null,
                    "translation": "Давным-давно",
                    "successfulAttempts": 0,
                    "attempts": 1
                },
                {
                    "phraseId": 1,
                    "phrase": "sort",
                    "correctedPhrase": null,
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "successfulAttempts": 1,
                    "attempts": 1
                }
            ]
        }
    },
    {
        "phraseNumber": 1,
        "context": {
            "id": 0,
            "movieId": 0,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort...",
            "link": "0.mp4",
            "phrases": [
                {
                    "phraseId": 0,
                    "phrase": "Once upon a time",
                    "correctedPhrase": null,
                    "type": null,
                    "translation": "Давным-давно",
                    "successfulAttempts": 0,
                    "attempts": 1
                },
                {
                    "phraseId": 1,
                    "phrase": "sort",
                    "correctedPhrase": null,
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "successfulAttempts": 1,
                    "attempts": 1
                }
            ]
        }
    },
    {
        "phraseNumber": 0,
        "context": {
            "id": 1,
            "movieId": 0,
            "context": "She was locked away in a castle... guarded by a terrible fire-breathing dragon.",
            "link": "1.mp4",
            "phrases": [
                {
                    "phraseId": 2,
                    "phrase": "castle",
                    "correctedPhrase": "a castle",
                    "type": "имя существительное",
                    "translation": "замок | дворец",
                    "successfulAttempts": 2,
                    "attempts": 2
                }
            ]
        }
    },
    {
        "phraseNumber": 0,
        "context": {
            "id": 2,
            "movieId": 0,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort... which could only be broken by love's first kiss. She was locked away in a castle... guarded by a terrible fire-breathing dragon. Many brave knights had attempted to free her from this deadful prison. But none prevailed. She waited in the dragon's keep... in the highest room of the tallest tower... for her true love and true love's first kiss. Like that's ever gonna happen.",
            "link": "2.mp4",
            "phrases": [
                {
                    "phraseId": 3,
                    "phrase": "Once upon a time",
                    "correctedPhrase": null,
                    "type": null,
                    "translation": "Давным-давно",
                    "successfulAttempts": 2,
                    "attempts": 3
                },
                {
                    "phraseId": 4,
                    "phrase": "sort",
                    "correctedPhrase": null,
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "successfulAttempts": 0,
                    "attempts": 2
                },
                {
                    "phraseId": 5,
                    "phrase": "castle",
                    "correctedPhrase": "a castle",
                    "type": "имя существительное",
                    "translation": "замок | дворец",
                    "successfulAttempts": 3,
                    "attempts": 5
                }
            ]
        }
    },
    {
        "phraseNumber": 1,
        "context": {
            "id": 2,
            "movieId": 0,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort... which could only be broken by love's first kiss. She was locked away in a castle... guarded by a terrible fire-breathing dragon. Many brave knights had attempted to free her from this deadful prison. But none prevailed. She waited in the dragon's keep... in the highest room of the tallest tower... for her true love and true love's first kiss. Like that's ever gonna happen.",
            "link": "2.mp4",
            "phrases": [
                {
                    "phraseId": 3,
                    "phrase": "Once upon a time",
                    "correctedPhrase": null,
                    "type": null,
                    "translation": "Давным-давно",
                    "successfulAttempts": 2,
                    "attempts": 3
                },
                {
                    "phraseId": 4,
                    "phrase": "sort",
                    "correctedPhrase": null,
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "successfulAttempts": 0,
                    "attempts": 2
                },
                {
                    "phraseId": 5,
                    "phrase": "castle",
                    "correctedPhrase": "a castle",
                    "type": "имя существительное",
                    "translation": "замок | дворец",
                    "successfulAttempts": 3,
                    "attempts": 5
                }
            ]
        }
    },
    {
        "phraseNumber": 2,
        "context": {
            "id": 2,
            "movieId": 0,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort... which could only be broken by love's first kiss. She was locked away in a castle... guarded by a terrible fire-breathing dragon. Many brave knights had attempted to free her from this deadful prison. But none prevailed. She waited in the dragon's keep... in the highest room of the tallest tower... for her true love and true love's first kiss. Like that's ever gonna happen.",
            "link": "2.mp4",
            "phrases": [
                {
                    "phraseId": 3,
                    "phrase": "Once upon a time",
                    "correctedPhrase": null,
                    "type": null,
                    "translation": "Давным-давно",
                    "successfulAttempts": 2,
                    "attempts": 3
                },
                {
                    "phraseId": 4,
                    "phrase": "sort",
                    "correctedPhrase": null,
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "successfulAttempts": 0,
                    "attempts": 2
                },
                {
                    "phraseId": 5,
                    "phrase": "castle",
                    "correctedPhrase": "a castle",
                    "type": "имя существительное",
                    "translation": "замок | дворец",
                    "successfulAttempts": 3,
                    "attempts": 5
                }
            ]
        }
    }
];


