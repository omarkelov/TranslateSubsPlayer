const app = Vue.createApp({
    data() {
        return {
            username: null,
            rawMovies: null,
            rawMovie: null,
            movies: null,
            movie: null,
            test: null,
            phraseTest: null,

            loginFormName: '',
            loginFormPassword: '',

            testPhraseTranslation: '',
            testPhraseContext: '',
            testData: null,
            indexTest: null,
            currentIndexCount: null,
            tooltip: null
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
    computed: {
        computedSubtitles() {
            let subtitles = '';
            this.rawMovie.lines.forEach(line => {
                subtitles +=
                    '<div class="subtitle">' +
                        '<p class="subtitle-time"><span>' + line.start + '</span> --> <span>' + line.end + '</span></p>' +
                        '<p class="subtitle-text" data-start-time="' + line.start + '" data-end-time="' + line.end + '" @mousedown="onSubtitlesTextMouseDown(' + line.start + ')" @mouseup="onSubtitlesTextMouseUp(' + line.end + ')">' + line.text + '</p>' +
                    '</div>';
            });
            return {
                data() {
                    return {
                        startTime: null
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
                    onSubtitlesTextMouseDown(startTime) {
                        if (event.which != 1) {
                            return;
                        }
                        this.startTime = startTime;
                    },
                    onSubtitlesTextMouseUp(endTime) {
                        if (event.which != 1) {
                            return;
                        }
                        let rawMovieContext = document.getElementById('raw-movie-context');
                        rawMovieContext.value = this.getSelectedText().replace(/[\r\n]+/g, ' ');
                        rawMovieContext.dispatchEvent(new Event('input'));
//                        alert(this.startTime + ' --> ' + endTime);
                    }
                },
                template: '<div id="raw-movie-subtitles" class="raw-movie-subtitles" style="height: ' + this.getRawMovieBoxHeight() + 'px">' + subtitles + '</div>'
            }
        },
        computedContexts() {
            let lis = '';
            this.movie.contexts.forEach(context => {
                contextHtml = context.context;
                context.phrases.forEach(phrase => {
                    contextHtml = contextHtml.replace(phrase.phrase,
                        `<span @mouseover.prevent="showTooltip" @mouseout.prevent="hideTooltip" data-tooltip='${phrase.translation}'>` + phrase.phrase + `</span>`);
                });
                lis += `<li>` + contextHtml +
                    `<div class="button watch-button" @click.prevent="toggle"></div>
                    <div class="trailer">
                       <video src="sea.mp4" controls="controls"></video>
                       <img src="close.png" class="close" @click.prevent="toggle" alt="close">
                    </div>
                    <div class="button edit-button" @click.prevent="showMsg"></div>
                    <div class="button delete-button" onclick="remove(this)"></div>` + `</li>`
            });
            return {
                data() {
                    return {
                        tooltipElem: null
                    }
                },
                template: `<ol>` + lis + `</ol>`,
                methods: {
                    showTooltip(e) {
                        let target = event.target;

                        let tooltipHtml = target.dataset.tooltip;
                        if (!tooltipHtml) return;

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
                    toggle() {
                        let trailer = document.querySelector(".trailer")
                        let video = document.querySelector("video")
                        trailer.classList.toggle("active");
                        video.pause();
                        video.currentTime = 0;
                    },
                    showMsg() {
                        alert("Edit");
                    }
                }
            }
        }
    },
    methods: {
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
            this.testData = json; // todo!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
            this.currentIndexCount = 0;
            this.indexTest = this.test.phraseIds[this.currentIndexCount];
            this.testData.contexts.forEach(context => {
                for (let i = 0; i <context.phrases.length; i++){
                    if (context.phrases[i].id === this.indexTest){
                        this.testPhraseContext = context.context;
                        this.testPhraseTranslation = context.phrases[i].translation;
                    }
                }
                }
            );
        },
        showHideContext(element_id) {
            if (document.getElementById(element_id)) {
                let obj = document.getElementById(element_id);
                let cont = document.getElementById("show-context");
                if (obj.style.display !== "block") {
                    obj.style.display = "block";
                    cont.style.display = "none";
                } else {
                    obj.style.display = "none";
                    cont.style.display = "block";
                }
            } else alert("Элемент с id: " + element_id + " не найден!");
        },
        hideHint(element_id){
            if (document.getElementById(element_id)) {
                let obj = document.getElementById(element_id);
                let cont = document.getElementById("show-context");
                obj.style.display = "none";
                cont.style.display = "block";
            } else alert("Элемент с id: " + element_id + " не найден!");
        },
        knowWord(event) {
            alert("Know this word");
            this.changePhrase();

        },
        doNotKnowWord(event) {
            alert("Don't know this word");
            this.changePhrase();
        },
        changePhrase() {
            this.hideHint('block_id');
            this.currentIndexCount++;
            if (this.currentIndexCount < this.test.phraseIds.length){
                this.indexTest = this.test.phraseIds[this.currentIndexCount];
                this.testData.contexts.forEach(context => {
                        for (let i = 0; i <context.phrases.length; i++){
                            if (context.phrases[i].id === this.indexTest){
                                this.testPhraseContext = context.context;
                                this.testPhraseTranslation = context.phrases[i].translation;
                            }
                        }
                    }
                );
            } else {
                alert("You've completed the test");
            }
        },
        toggle() {
            let trailer = document.querySelector(".trailer")
            let video = document.querySelector("video")
            trailer.classList.toggle("active");
            video.pause();
            video.currentTime = 0;
        }
    }
});

app.mount('#vue-app');

function remove(el) {
    el.parentNode.remove();
}
