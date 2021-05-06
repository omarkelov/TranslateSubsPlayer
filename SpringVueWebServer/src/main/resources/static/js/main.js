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
    computed: {
        computedContexts() {
            let lis = '';
            this.movie.contexts.forEach(context => {
                contextHtml = context.context;
                context.phrases.forEach(phrase => {
                    contextHtml = contextHtml.replace(phrase.phrase,
                        `<span data-tooltip=${phrase.phrase}>` + phrase.phrase + `</span>`);
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
                template: `<ol>` + lis + `</ol>`,
                methods: {
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
            this.movies = null;
            this.movie = null;
            this.test = null;
        },
        onRawMoviesClicked(event) {
            // todo
        },
        showRawMovies(json) {
            // todo
        },
        onRawMovieClicked(event) {
            // todo
        },
        showRawMovie(json) {
            // todo
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
            this.testData = json;
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

let tooltipElem;
document.onmouseover = function (event) {
    let target = event.target;

    let tooltipHtml = target.dataset.tooltip;
    if (!tooltipHtml) return;

    tooltipElem = document.createElement('div');
    tooltipElem.className = 'tooltip';
    tooltipElem.innerHTML = tooltipHtml;
    document.body.append(tooltipElem);

    let coords = target.getBoundingClientRect();

    let left = coords.left + (target.offsetWidth - tooltipElem.offsetWidth) / 2;
    if (left < 0) left = 0;

    let top = coords.top - tooltipElem.offsetHeight - 5;
    if (top < 0) {
        top = coords.top + target.offsetHeight + 5;
    }

    tooltipElem.style.left = left + 'px';
    tooltipElem.style.top = top + 'px';
};

document.onmouseout = function (e) {

    if (tooltipElem) {
        tooltipElem.remove();
        tooltipElem = null;
    }

};

function remove(el) {
    el.parentNode.remove();
}

