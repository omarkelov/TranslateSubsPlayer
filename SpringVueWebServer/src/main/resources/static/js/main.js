const app = Vue.createApp({
    data() {
        return {
            username: null,
            rawMovies: null,
            rawMovie: null,
            movies: null,
            movie: null,
            test: null,

            loginFormName: '',
            loginFormPassword: '',

            tooltipElem: null,
            contextVideoLink: null
        }
    },
    beforeMount() {
        if (serverUsername) {
            this.username = serverUsername;

            if (serverRawMovies) {
                this.showRawMovies(JSON.parse(serverRawMovies));
            } else if (serverRawMovie) {
                this.showRawMovie(JSON.parse(serverRawMovie));
            } else if (serverMovies) {
                this.showMovies(JSON.parse(serverMovies));
            } else if (serverMovie) {
                this.showMovie(JSON.parse(serverMovie));
            } else if (serverTest) {
                this.showTest(JSON.parse(serverTest));
            }
        } else {
            this.showLogin();
        }
    },
    created() {
        window.addEventListener("resize", this.onWindowResize);
    },
    updated() {
        if (this.contextVideoLink) {
            document.getElementById('player-video').play();
        }
    },
    computed: {
        computedSubtitles() {
            let rawMovie = this.rawMovie;
            let subtitles = '';
            for (let i = 0; i < rawMovie.lines.length; i++) {
                let line = rawMovie.lines[i];
                subtitle = line.text;
                rawMovie.phrases.forEach(phrase => {
                    if (phrase.phrase.lineId == i) { // todo optimize by making this cycle first?
                        subtitle = subtitle.replace(phrase.phrase.phrase, '<span class="translated-phrase">' + phrase.phrase.phrase + '</span>');
                    }
                });
                subtitles +=
                    '<div class="subtitle">' +
                        '<p class="subtitle-time"><span>' + line.start + '</span> --> <span>' + line.end + '</span></p>' +
                        '<p class="subtitle-text" data-start-time="' + line.start + '" data-end-time="' + line.end + '" @mousedown="onSubtitlesTextMouseDown(' + line.start + ', ' + line.end + ')" @mouseup="onSubtitlesTextMouseUp(' + line.start + ', ' + line.end + ')">' + subtitle + '</p>' +
                    '</div>';
            }
            return {
                data() {
                    return {
                        startTime: null,
                        endTime: null
                    }
                },
                methods: {
                    getSelectedText() {
                        if (window.getSelection) {
                            return window.getSelection().toString();
                        }
                        if (document.selection && document.selection.type != 'Control') {
                            return document.selection.createRange().text;
                        }
                        return '';
                    },
                    clearSelection() {
                        if (window.getSelection) {
                            if (window.getSelection().empty) {
                                window.getSelection().empty();
                            } else if (window.getSelection().removeAllRanges) {
                                window.getSelection().removeAllRanges();
                            }
                        } else if (document.selection) {
                            document.selection.empty();
                        }
                    },
                    onSubtitlesTextMouseDown(startTime, endTime) {
                        if (event.which != 1) {
                            return;
                        }
                        this.startTime = startTime;
                        this.endTime = endTime;
                    },
                    onSubtitlesTextMouseUp(startTime, endTime) {
                        if (event.which != 1) {
                            return;
                        }
                        let rawMovieContext = document.getElementById('raw-movie-context');
                        let selectedText = this.getSelectedText();
                        //this.clearSelection();
                        if (!selectedText) {
                            return;
                        }
                        let rawMoviePhrases = document.getElementById('raw-movie-phrases-body');
                        rawMoviePhrases.innerHTML =
                            '<tr>' +
                                '<th><label>Phrase</label></th>' +
                                '<th><label>Corrected Phrase</label></th>' +
                                '<th><label>Translation</label></th>' +
                            '</tr>';
                        let plainSelectedText = selectedText;
                        let phrasesPresent = false;
                        rawMovie.phrases.forEach(phrase => {
                            if (plainSelectedText.includes(phrase.phrase.phrase)) {
                                phrasesPresent = true;
                                let translation;
                                if (!phrase.phrase.translation.groups || phrase.phrase.translation.groups.length == 0) {
                                    translation = '<input type="text" value="' + phrase.phrase.translation.main + '"></input>';
                                } else {
                                    let optgroups = '<optgroup label="main"><option>' + phrase.phrase.translation.main + '</option></optgroup>';
                                    phrase.phrase.translation.groups.forEach(group => {
                                        if (!group.variants || group.variants.length == 0) {
                                            return;
                                        }
                                        let options = '';
                                        group.variants.forEach(variant => options += '<option>' + variant + '</option>');
                                        optgroups +=
                                            '<optgroup label="' + group.partOfSpeech + '">' + options + '</optgroup>';
                                    });
                                    translation = '<select type="text">' + optgroups + '</select>';
                                }
                                rawMoviePhrases.innerHTML +=
                                    '<tr>' +
                                        '<td><input type="text" value="' + phrase.phrase.phrase + '" readonly></input></td>' +
                                        '<td><input type="text" value="' + phrase.phrase.phrase + '"></input></td>' +
                                        '<td>' + translation + '</td>' +
                                        '<td><div class="raw-movie-phrases-delete-row"' +
                                                  'onclick="this.parentElement.parentElement.remove();' +
                                                           'if (document.getElementById(\'raw-movie-phrases-body\').rows.length < 2) {' +
                                                               'document.getElementById(\'raw-movie-phrases-shell\').style.display = \'none\';' +
                                                               'document.getElementById(\'raw-movie-context\').innerHTML = \'Select some subtitles from the left section\';' +
                                                           '}"></div></td>' +
                                    '</tr>';
                            }
                            selectedText = selectedText.replace(phrase.phrase.phrase, '<span class="translated-phrase">' + phrase.phrase.phrase + '</span>');
                        });
                        if (!phrasesPresent) {
                            rawMovieContext.innerHTML = 'You should select subtitles with translated phrases';
                            return;
                        }
                        rawMovieContext.innerHTML = selectedText.replace(/[\r\n]+/g, ' ');
                        rawMovieContext.dispatchEvent(new Event('input'));
                        rawMovieContext.setAttribute('data-start-time', Math.min(this.startTime, this.endTime, startTime, endTime));
                        rawMovieContext.setAttribute('data-end-time', Math.max(this.startTime, this.endTime, startTime, endTime));
                        document.getElementById('raw-movie-phrases-shell').style.display = 'block';
                    }
                },
                template: '<div id="raw-movie-subtitles" class="raw-movie-subtitles" style="height: ' + this.getRawMovieBoxHeight() + 'px">' + subtitles + '</div>'
            }
        },
        computedTestContext() {
            let showTooltipFunction = this.showTooltip;
            let hideTooltipFunction = this.hideTooltip;
            let showVideoFunction = this.showVideo;
            return {
                template: '<div class="test-context">' + this.generateContextHtml(this.test.context, false) + '</div>',
                methods: {
                    showTooltip() {
                        showTooltipFunction(event.target);
                    },
                    hideTooltip() {
                        hideTooltipFunction();
                    },
                    watchContextVideo(contextVideoLink) {
                        showVideoFunction(contextVideoLink);
                    }
                }
            }
        },
        computedContexts() {
            let lis = '';
            this.movie.contexts.forEach(context => {
                lis += '<li class="movie-context">' + this.generateContextHtml(context, true) + '</li>'
            });
            let showTooltipFunction = this.showTooltip;
            let hideTooltipFunction = this.hideTooltip;
            let showVideoFunction = this.showVideo;
            return {
                template: '<ol>' + lis + '</ol>',
                methods: {
                    showTooltip() {
                        showTooltipFunction(event.target);
                    },
                    hideTooltip() {
                        hideTooltipFunction();
                    },
                    watchContextVideo(contextVideoLink) {
                        showVideoFunction(contextVideoLink);
                    },
                    removeContext() {
                        if (confirm('Are you sure you want to delete this context?')) {
                            let target = event.target;
                            let contextId = target.getAttribute("data-context-id");
                            fetch('/contexts/' + contextId, {method: 'DELETE'})
                                .then(response => {
                                    if (response.status == 204) {
                                        target.parentNode.parentNode.remove();
                                    } else {
                                        alert('Cannot remove the context: status code is ' + response.status);
                                    }
                                });
                        }
                    }
                }
            }
        }
    },
    methods: {
        showVideo(contextVideoLink) {
            this.contextVideoLink = contextVideoLink;
        },
        hideVideo() {
            this.contextVideoLink = null;
        },
        showTooltip(target) {
            let tooltipHtml = target.querySelector('.tooltip-text').innerHTML;
            if (!tooltipHtml) {
                return;
            }
            this.tooltipElem = document.createElement('div');
            this.tooltipElem.className = 'tooltip';
            this.tooltipElem.innerHTML = tooltipHtml;
            document.body.append(this.tooltipElem);
            let coords = target.getBoundingClientRect();
            let left = coords.left + (target.offsetWidth - this.tooltipElem.offsetWidth) / 2;
            if (left < 0) left = 0;
            let top = coords.top - this.tooltipElem.offsetHeight - 5;
            if (top < 0) {
                top = coords.top + target.offsetHeight + 5;
            }
            this.tooltipElem.style.left = left + 'px';
            this.tooltipElem.style.top = top + 'px';
        },
        hideTooltip() {
            if (this.tooltipElem) {
                this.tooltipElem.remove();
                this.tooltipElem = null;
            }
        },
        onWindowResize(e) {
            let rawMovieSubtitles = document.getElementById('raw-movie-subtitles');
            if (rawMovieSubtitles) {
                rawMovieSubtitles.setAttribute('style', 'height: ' + this.getRawMovieBoxHeight() + 'px');
            }
            let rawMovieContext = document.getElementById('raw-movie-context');
            if (rawMovieContext) {
                rawMovieContext.setAttribute('style', 'height: ' + this.getRawMovieBoxHeight() + 'px');
            }
        },
        getRawMovieBoxHeight() {
            return window.innerHeight - 175;
        },
        resizeRawMovieContext() {
            let rawMovieContext = document.getElementById('raw-movie-context');
            rawMovieContext.style.height = '100px';
            rawMovieContext.style.height = (rawMovieContext.scrollHeight + 10) + 'px';
        },
        onLoginClicked() {
            fetch('/login', {
                method: 'POST',
                headers: {
                    'Accept': '*/*',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'username=' + this.loginFormName + '&password=' + this.loginFormPassword
            }).then(response => {
                if (response.status == 200) {
                    this.username = this.loginFormName;
                    this.loginFormName = ''
                    this.loginFormPassword = ''
                    this.onMoviesClicked();
                } else {
                    alert('status ' + response.status);
                }
            });
        },
        onLogoutClicked() {
            fetch('/logout', {method: 'GET'}).then(response => {
                if (response.status == 200) {
                    this.hideEverything();
                    this.showLogin();
                } else {
                    alert('status ' + response.status);
                }
            });
        },
        showLogin() {
            this.username = null;
            window.history.pushState({}, '', '/login');
            document.title = 'Login | Translate Subtitles Player';
        },
        hideEverything() {
            this.rawMovies = null;
            this.rawMovie = null;
            this.movies = null;
            this.movie = null;
            this.test = null;
        },
        onRawMoviesClicked(event) {
            fetch('/raw-movies/', {method: 'GET'})
                .then(response => response.json())
                .then(json => this.showRawMovies(json));
        },
        showRawMovies(json) {
            window.history.pushState({}, '', '/raw-movies');
            document.title = 'Raw Movies | Translate Subtitles Player';
            this.hideEverything();
            this.rawMovies = json;
        },
        removeRawMovie() {
            if (confirm('Are you sure you want to delete this raw movie?')) {
                let target = event.target;
                let rawMovieId = target.getAttribute("data-raw-movie-id");
                fetch('/raw-movies/' + rawMovieId, {method: 'DELETE'})
                    .then(response => {
                        if (response.status == 204) {
                            target.parentNode.parentNode.remove();
                        } else {
                            alert('Cannot remove the raw movie: status code is ' + response.status);
                        }
                    });
            }
        },
        onRawMovieClicked(event) {
            let rawMovieUrl = event.target.getAttribute('href');
                fetch(rawMovieUrl, {method: 'GET'})
                    .then(response => response.json())
                    .then(json => this.showRawMovie(json));
        },
        showRawMovie(json) {
            window.history.pushState({}, '', '/raw-movies/' + json.id);
            document.title = json.videoFilePath + ' | Translate Subtitles Player';
            this.hideEverything();
            this.rawMovie = json;
            fetch('/movies/', {method: 'GET'})
                .then(response => response.json())
                .then(json => {
                    let options = '';
                    json.forEach(movie => options += '<option value="' + movie.name + '"/>');
                    document.getElementById('dictionary-data-list').innerHTML = options;
                });
        },
        onMoviesClicked() {
            fetch('/movies/', {method: 'GET'})
                .then(response => response.json())
                .then(json => this.showMovies(json));
        },
        showMovies(json) {
            window.history.pushState({}, '', '/movies');
            document.title = 'Movies | Translate Subtitles Player';
            this.hideEverything();
            this.movies = json;
        },
        removeMovie() {
            if (confirm('Are you sure you want to delete this movie?')) {
                let target = event.target;
                let movieName = target.getAttribute("data-movie-name");
                fetch('/movies/' + movieName, {method: 'DELETE'})
                    .then(response => {
                        if (response.status == 204) {
                            target.parentNode.parentNode.remove();
                        } else {
                            alert('Cannot remove the movie: status code is ' + response.status);
                        }
                    });
            }
        },
        onMovieClicked(event) {
            let movieUrl = event.target.getAttribute('href');
            fetch(movieUrl, {method: 'GET'})
                .then(response => response.json())
                .then(json => this.showMovie(json));
        },
        showMovie(json) {
            window.history.pushState({}, '', '/movies/' + json.name);
            document.title = json.name + ' | Translate Subtitles Player';
            this.hideEverything();
            this.movie = json;
        },
        generateContextHtml(context, actionsAllowed) {
            contextHtml = context.context;
            context.phrases.forEach(phrase => {
                let tooltipAttempts = '';
                if (phrase.phraseStats && phrase.phraseStats.attempts && phrase.phraseStats.attempts > 0) {
                    tooltipAttempts = ' (' + phrase.phraseStats.successfulAttempts + ' / ' + phrase.phraseStats.attempts + ')';
                }
                contextHtml = contextHtml.replace(phrase.phrase,
                    '<span class="translated-phrase" @mouseover.prevent="showTooltip" @mouseout.prevent="hideTooltip">' + phrase.phrase +
                        '<span class="tooltip-text">' +
                            '<span class="tooltip-translation">' + phrase.translation + '</span>' +
                            '<span class="tooltip-translation">' + tooltipAttempts + '</span>' +
                        '</span>' +
                    '</span>');
            });
            let contextButtons = actionsAllowed ?
                '<div class="action-buttons"><div class="video-button" @click="watchContextVideo(\'' + context.link + '\')"></div><div class="remove-button" data-context-id="' + context.id + '" @click="removeContext"></div></div>' :
                '<div class="video-button" @click="watchContextVideo(\'' + context.link + '\')"></div>';
            return '<span>' + contextHtml + '</span>' + contextButtons;
        },
        onTestClicked(event) {
            let testUrl = event.target.getAttribute('href');
            fetch(testUrl, {method: 'GET'})
                .then(response => response.json())
                .then(json => this.showTest(json));
        },
        showTest(json) {
            window.history.pushState({}, '', '/movies/' + json.name + '/test');
            document.title = json.name + ' Test | Translate Subtitles Player';
            this.hideEverything();
            this.test = json;
            this.test['phraseIndex'] = 0;
            this.test['phraseTested'] = null;
            this.showNextTestPhrase();
        },
        showNextTestPhrase() {
            if (this.test.phraseIndex >= this.test.phraseIds.length) {
                alert("You've completed the test");
                return;
            }
            fetch('/context?phraseId=' + this.test.phraseIds[this.test.phraseIndex], {method: 'GET'})
                .then(response => response.json())
                .then(json => {
                    this.test['context'] = json;
                    json.phrases.forEach(phrase => {
                        if (phrase.id == this.test.phraseIds[this.test.phraseIndex]) {
                            this.test['phrase'] = phrase;
                        }
                    });
                    this.test.phraseIndex++;
                    this.test.userTranslation = ''
                    this.test.phraseTested = false;
                });
        },
        onForgetTranslationButtonClicked() {
            this.onTranslationButtonClicked(false);
        },
        onSubmitTranslationButtonClicked() {
            let isCorrect = this.test.userTranslation == this.test.phrase.phrase || this.test.userTranslation == this.test.phrase.correctedPhrase;
            alert('You are ' + (isCorrect ? 'correct' : 'wrong'));
            this.onTranslationButtonClicked(isCorrect);
        },
        onTranslationButtonClicked(isCorrect) {
            this.test.phraseTested = true;
            fetch('/phrases/' + this.test.phrase.id + '?correct=' + isCorrect, {method: 'PATCH'});
        },
        onNextPhraseButtonClicked() {
            this.showNextTestPhrase();
        },
        addToDictionary() {
            let dictionarySelect = document.getElementById('dictionary');
            if (!dictionarySelect.value) {
                alert('You must provide dictionary name');
                return;
            }
            let movie = {
                name: dictionarySelect.value,
                videoFilePath: this.rawMovie.videoFilePath,
                lang: 'en'
            };
            fetch('/movies', {
                method: 'PUT',
                body: JSON.stringify(movie)
            }).then(response => {
                if (response.status != 204) {
                    alert('Could not create a dictionary: status code is ' + response.status);
                    return;
                }
                let rawMovieContext = document.getElementById('raw-movie-context');
                let context = {
                    startTime: rawMovieContext.getAttribute('data-start-time'),
                    endTime: rawMovieContext.getAttribute('data-end-time'),
                    context: document.getElementById('raw-movie-context').innerHTML.replace(/(<([^>]+)>)/ig, ''),
                    phrases: []
                };
                let table = document.getElementById('raw-movie-phrases-body');
                for (let i = 1, row; row = table.rows[i]; i++) {
                    let type = null;
                    let translation = null;
                    let translationNode = row.cells[2].firstChild;
                    if (translationNode.tagName == 'INPUT') {
                        translation = translationNode.value;
                    } else {
                        translation = translationNode.options[translationNode.selectedIndex].text;
                        type = translationNode.querySelector('option:checked').parentElement.label;
                        if (type == 'main') {
                            type = null;
                        }
                    }
                    context.phrases.push({
                        phrase: row.cells[0].firstChild.value,
                        correctedPhrase: row.cells[1].firstChild.value,
                        type: type,
                        translation: translation
                    });
                }
                fetch('/contexts?movieName=' + movie.name, {
                    method: 'POST',
                    body: JSON.stringify(context)
                }).then(response => {
                    if (response.status != 204) {
                        alert('Could not create a context: status code is ' + response.status);
                        return;
                    }
                    document.getElementById('raw-movie-context').innerHTML = 'Select some subtitles from the left section';
                    document.getElementById('raw-movie-phrases-shell').style.display = 'none';
                    alert('The context is successfully created');
                });
            });
        }
    }
});

app.mount('#vue-app');
