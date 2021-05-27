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
            testPhraseId: null,
            testData: null,
            indexTest: null,
            currentIndexCount: null,

            current_phrase_id: 0,
            text: '',
            phrase_clicked: null,
            phrase_transl: [],
            phrase_main_translation: null,
            phrase_type: null,
            phrase_context: '',
            prev: '',
            key: '',
            partOfSpeechOptions: []
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
                        `<span @mouseover.prevent="showTooltip" @mouseout.prevent="hideTooltip" data-tooltip='${phrase.translation}'>` + phrase.phrase + `</span>`);
                });
                lis += `<li>` + contextHtml +
                    `<div class="button watch-button" @click.prevent="toggle"></div>
                    <div class="trailer">
                       <video src="sea.mp4" controls="controls"></video>
                       <img src="close.png" class="close" @click.prevent="toggle" alt="close">
                    </div>
                    <div class="button edit-button" @click.prevent="showMsg"></div>
                    <div id="delBtn" class="button delete-button" data-contextId="${context.id}" @click.prevent="onDeleteContextClicked"></div>` + `</li>`
            });
            return {
                data() {
                    return {
                        tooltipElem: null
                    }
                },
                template: `<ol>` + lis + `</ol>`,
                methods: {
                    onDeleteContextClicked(event) {
                        if (confirm('Are yoe sure you want to delete this context?')) {
                            let target = event.target;
                            let contextId = target.getAttribute("data-contextId");
                            fetch('/contexts/' + contextId, {method: 'DELETE'}).then(response => {
                                if (response.ok) {
                                    target.parentNode.remove();
                                } else {
                                    alert('status ' + response.status);
                                }
                            });
                        }
                    },
                    showTooltip(event) {
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
        },
        computedLines() {
            let lis = '';
            let lineHtml;
            this.phrase_clicked = this.rawMovie.phrases[0].phrase.phrase;
            this.phrase_main_translation = this.rawMovie.phrases[0].phrase.translation.main;
            for (let i = 0; i < this.rawMovie.lines.length; i++) {
                lineHtml = this.rawMovie.lines[i].text;
                for (let j = 0; j < this.rawMovie.phrases.length; j++) {
                    if (this.rawMovie.phrases[j].phrase.lineId === i) {
                        lineHtml = lineHtml.replace(this.rawMovie.phrases[j].phrase.phrase,
                            `<span style="color: red">` + this.rawMovie.phrases[j].phrase.phrase + `</span>`)
                    }
                }
                lis += `<div>` + lineHtml + ` ` + `</div>`;
            }
            return {
                template: `<div>` + lis + `</div>`,
                methods: {
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
            this.rawMovie = null;
            this.rawMovies = null;
        },
        onRawMoviesClicked(event) {
            fetch('/raw-movies/', {method: 'GET'})
                .then(response => response.json())
                .then(json => this.showRawMovies(json));
        },
        showRawMovies(json) {
            window.history.pushState({}, '', '/raw-movies');
            document.title = 'Movies | Translate Subtitles Player';
            this.hideEverything();
            this.rawMovies = json;
        },
        onRawMovieClicked(event) {
            let movieUrl = event.target.getAttribute('href');
            fetch('/raw-movies/' , {method: 'GET'})
                .then(response => response.json())
                .then(json => this.showRawMovie(json));
        },
        showRawMovie(json) {
            window.history.pushState({}, '', '/raw-movies/' + json.id);
            document.title = json.id + ' | Translate Subtitles Player';
            this.hideEverything();
            this.rawMovie = raw_movieex;
            this.text = this.rawMovie.lines;

            document.addEventListener('mouseup', event => {
                this.selectContext();
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
                    for (let i = 0; i < context.phrases.length; i++) {
                        if (context.phrases[i].id === this.indexTest) {
                            this.testPhraseContext = context.context;
                            this.testPhraseTranslation = context.phrases[i].translation;
                            this.testPhraseId = context.phrases[i].id;
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
        hideHint(element_id) {
            if (document.getElementById(element_id)) {
                let obj = document.getElementById(element_id);
                let cont = document.getElementById("show-context");
                obj.style.display = "none";
                cont.style.display = "block";
            } else alert("Элемент с id: " + element_id + " не найден!");
        },
        knowWord(event) {
            alert("Know this word");
            fetch('/phrases/' + this.testPhraseId + '?correct=true', {
                method: 'PATCH'
            });
            this.changePhrase();

        },
        doNotKnowWord(event) {
            alert("Don't know this word");
            fetch('/phrases/' + this.testPhraseId + '?correct=false', {
                method: 'PATCH'
            });
            this.changePhrase();
        },
        changePhrase() {
            this.hideHint('block_id');
            this.currentIndexCount++;
            if (this.currentIndexCount < this.test.phraseIds.length) {
                this.indexTest = this.test.phraseIds[this.currentIndexCount];
                this.testData.contexts.forEach(context => {
                        for (let i = 0; i < context.phrases.length; i++) {
                            if (context.phrases[i].id === this.indexTest) {
                                this.testPhraseContext = context.context;
                                this.testPhraseTranslation = context.phrases[i].translation;
                                this.testPhraseId = context.phrases[i].id;
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
        },
        selectContext() {
            if (window.getSelection().toString() !== '') {
                this.prev = window.getSelection().toString();
                this.phrase_context = window.getSelection().toString();
            } else {
                this.phrase_context = this.prev;
            }
        },
        onchange() {
            this.key = raw_movie.phrases[1].phrase.translation.groups[0].partOfSpeech
            console.log(this.key)
            // alert(this.key)
            alert(raw_movie.phrases[1].phrase.translation.groups[0].partOfSpeech)
        },
        someF() {
            alert("Clicked!");
            this.current_phrase_id++;
            if (this.current_phrase_id >= raw_movie.phrases.length) {
                this.current_phrase_id = 0;
            }
            this.phrase_clicked = raw_movie.phrases[this.current_phrase_id].phrase.phrase;
        }
    }
});


app.mount('#vue-app');

const raw_movieex = {
    "id": 2,
    "videoFilePath": "C:/Movies/Shrek",
    "lines": [
        {
            "start": 47580,
            "end": 52450,
            "text": "\"Once upon a time there was a lovely princess"
        },
        {
            "start": 52520,
            "end": 56290,
            "text": "\"But she had an enchantment upon her of a fearful sort..."
        },
        {
            "start": 56350,
            "end": 61220,
            "text": "\"which could only be broken by love\u0027s first kiss."
        },
        {
            "start": 61320,
            "end": 63660,
            "text": "\"She was locked away in a castle..."
        },
        {
            "start": 63760,
            "end": 67960,
            "text": "\"guarded by a terrible fire-breathing dragon."
        },
        {
            "start": 68060,
            "end": 72400,
            "text": "\"Many brave knights had attempted to free her from this deadful prison."
        },
        {
            "start": 72470,
            "end": 75270,
            "text": "\"but none prevailed."
        },
        {
            "start": 75370,
            "end": 77510,
            "text": "\"She waited in the dragon\u0027s keep..."
        },
        {
            "start": 77570,
            "end": 80510,
            "text": "\"in the highest room of the tallest tower..."
        },
        {
            "start": 80580,
            "end": 85150,
            "text": "for her true love and true love\u0027s first kiss. \""
        },
        {
            "start": 85210,
            "end": 88180,
            "text": "Like that\u0027s ever gonna happen"
        },
        {
            "start": 88280,
            "end": 91990,
            "text": "- What a load of -"
        },
        {
            "start": 92120,
            "end": 96690,
            "text": "Somebody once told me\nthe world is gonna roll me"
        },
        {
            "start": 96790,
            "end": 100760,
            "text": "I ain\u0027t the sharpest tool\nin the shed"
        },
        {
            "start": 100830,
            "end": 105730,
            "text": "She was lookin\u0027 kind of dumb\nwith her finger and her thumb"
        },
        {
            "start": 105840,
            "end": 109540,
            "text": "In the shape of an \"L\"\non the forehead"
        },
        {
            "start": 109610,
            "end": 112540,
            "text": "The years start comin\u0027\nand they don\u0027t stop commin\u0027"
        },
        {
            "start": 112640,
            "end": 115440,
            "text": "Fed to the rules\nand I hit the ground runnin\u0027"
        },
        {
            "start": 115540,
            "end": 117780,
            "text": "Didn\u0027t make sense\nnot live for fun"
        },
        {
            "start": 117880,
            "end": 120120,
            "text": "Your brain gets smart\nbut your head gets dumb"
        },
        {
            "start": 120180,
            "end": 122080,
            "text": "So much to do\nSo much to see"
        },
        {
            "start": 122180,
            "end": 124790,
            "text": "So what\u0027s wrong with\ntakin\u0027 the backstreets"
        },
        {
            "start": 124850,
            "end": 126890,
            "text": "You\u0027ll never know\nif you don\u0027t go"
        },
        {
            "start": 126960,
            "end": 129390,
            "text": "You\u0027ll never shine\nif you don\u0027t glow"
        },
        {
            "start": 129490,
            "end": 131460,
            "text": "Hey, now\nYou\u0027re an all-star"
        },
        {
            "start": 131490,
            "end": 133900,
            "text": "Get your game on, go play"
        },
        {
            "start": 134000,
            "end": 137900,
            "text": "Hey, now, you\u0027re a rock star\nGet the show on, get paid"
        },
        {
            "start": 137970,
            "end": 141940,
            "text": "And all that glitter is gold"
        },
        {
            "start": 142040,
            "end": 147480,
            "text": "Only shootin\u0027 stars\nbreak the mould"
        },
        {
            "start": 147580,
            "end": 149550,
            "text": "It\u0027s a cool place and they say it gets colder"
        },
        {
            "start": 149650,
            "end": 151810,
            "text": "You\u0027re bundled up now but wait till you get older"
        },
        {
            "start": 151850,
            "end": 154020,
            "text": "But the meteor men beg to differ"
        },
        {
            "start": 154080,
            "end": 155990,
            "text": "Judging by the hole\nin the satellite picture"
        },
        {
            "start": 156090,
            "end": 158790,
            "text": "The ice we skate\nis gettin\u0027 pretty thin"
        },
        {
            "start": 158920,
            "end": 160860,
            "text": "The water\u0027s getting warm\nso you might as well swim"
        },
        {
            "start": 160920,
            "end": 162830,
            "text": "My world\u0027s on fire\nHow \u0027bout yours"
        },
        {
            "start": 162960,
            "end": 165590,
            "text": "That\u0027s the way I like it\nand I\u0027ll never get bored"
        },
        {
            "start": 165690,
            "end": 168300,
            "text": "- Hey, now, you\u0027re an all-star"
        },
        {
            "start": 168360,
            "end": 172000,
            "text": "Get your game on go play"
        },
        {
            "start": 172070,
            "end": 175200,
            "text": "Hey, now you\u0027re a rock star\nGet the show on, get paid"
        },
        {
            "start": 175270,
            "end": 178970,
            "text": "And all that glitter is gold"
        },
        {
            "start": 179070,
            "end": 183980,
            "text": "Only shootin\u0027 stars break the mould"
        },
        {
            "start": 187880,
            "end": 189790,
            "text": "- Go!\n- Go!"
        },
        {
            "start": 189920,
            "end": 193020,
            "text": "- Go. Go. Go."
        },
        {
            "start": 193120,
            "end": 195660,
            "text": "Hey, now You\u0027re an all-star"
        },
        {
            "start": 195760,
            "end": 198830,
            "text": "Get your game on, go play"
        },
        {
            "start": 198930,
            "end": 203170,
            "text": "Hey, now, you\u0027re a rock star\nGet the show on, get paid"
        },
        {
            "start": 203270,
            "end": 207140,
            "text": "And all that glitter is gold"
        },
        {
            "start": 207240,
            "end": 212510,
            "text": "Only shootin\u0027 stars break the mould"
        },
        {
            "start": 217210,
            "end": 220480,
            "text": "- Think it\u0027s in there?\n- All right. Let\u0027s get it!"
        },
        {
            "start": 220550,
            "end": 223320,
            "text": "Whoa, Hold on. Do you know\nwhat thing can do to you?"
        },
        {
            "start": 223390,
            "end": 226320,
            "text": "Yeah, it\u0027ll grind\nyour bones for its bread."
        },
        {
            "start": 226420,
            "end": 230390,
            "text": "Yes, well, actually,\nthat would be a giant."
        },
        {
            "start": 230460,
            "end": 234530,
            "text": "Now, ogres-\nThey\u0027re much worse."
        },
        {
            "start": 234630,
            "end": 237530,
            "text": "They\u0027ll make a suit\nfrom your freshly peeled skin."
        },
        {
            "start": 237600,
            "end": 239700,
            "text": "- No!\n- They\u0027ll shave your liver."
        },
        {
            "start": 239800,
            "end": 243040,
            "text": "Squeeze the\njelly from your eyes!"
        },
        {
            "start": 243140,
            "end": 245670,
            "text": "- Actualy, it\u0027s quite good on toast.\n- Back! Back, beast!"
        },
        {
            "start": 245740,
            "end": 247880,
            "text": "Back! I warn ya!"
        },
        {
            "start": 250780,
            "end": 254220,
            "text": "-Right."
        },
        {
            "start": 269030,
            "end": 273570,
            "text": "This is the part\nwhere you run away."
        },
        {
            "start": 280380,
            "end": 283410,
            "text": "And stay out!"
        },
        {
            "start": 285280,
            "end": 288350,
            "text": "\"Wanted. Fairy tale creatures. \""
        },
        {
            "start": 293790,
            "end": 296290,
            "text": "All right. This one\u0027s full"
        },
        {
            "start": 296890,
            "end": 298990,
            "text": "- Take it away!"
        },
        {
            "start": 299100,
            "end": 302930,
            "text": "- Move it along. Come on! Get up!\n- Next!"
        },
        {
            "start": 303000,
            "end": 305940,
            "text": "Give me that! Your flying days are over."
        },
        {
            "start": 306000,
            "end": 308640,
            "text": "That\u0027s 20 pieces of silver\nfor the witch. Next!"
        },
        {
            "start": 308700,
            "end": 311470,
            "text": "- Get up! Come on!\n- Twenty pieces."
        },
        {
            "start": 311540,
            "end": 313910,
            "text": "- Sit down there!"
        },
        {
            "start": 313980,
            "end": 316510,
            "text": "- Keep quiet!"
        },
        {
            "start": 316580,
            "end": 319480,
            "text": "This cage is too small."
        },
        {
            "start": 319550,
            "end": 321620,
            "text": "Please don\u0027t turn me in.\nI\u0027ll never be stubborn again."
        },
        {
            "start": 321720,
            "end": 323720,
            "text": "I can change.\nPlease! Give ma another chance!"
        },
        {
            "start": 323820,
            "end": 326160,
            "text": "-Oh, shut up.\n-Oh! - Next!"
        },
        {
            "start": 326260,
            "end": 329130,
            "text": "-What have you got?\n- This little wooden puppet."
        },
        {
            "start": 329230,
            "end": 334130,
            "text": "I\u0027m not a puppet. I\u0027m a real boy."
        },
        {
            "start": 334200,
            "end": 336600,
            "text": "Five shillings forthe possessed toy."
        },
        {
            "start": 336700,
            "end": 338770,
            "text": "- Take it away.\n- Father, please! Don\u0027t let them do this!"
        },
        {
            "start": 338830,
            "end": 340770,
            "text": "- Help me!\n- Next. What have you got?"
        },
        {
            "start": 340870,
            "end": 343470,
            "text": "- Well, I\u0027ve got a talking donkey."
        },
        {
            "start": 343540,
            "end": 348140,
            "text": "Right. Well, that\u0027s good for\nten shillings, if you can prouve it."
        },
        {
            "start": 348210,
            "end": 352110,
            "text": "Oh, go ahead, little fella."
        },
        {
            "start": 352210,
            "end": 354120,
            "text": "Well?"
        },
        {
            "start": 354180,
            "end": 356920,
            "text": "Oh, oh, he\u0027s just-\nHe\u0027s just a little nervous."
        },
        {
            "start": 356990,
            "end": 360960,
            "text": "He\u0027s really quite a chatterbox. Talk, you boneheaded dolt-"
        },
        {
            "start": 361060,
            "end": 363660,
            "text": "- That\u0027s it. I\u0027ve heard enough. Guards!\n-No, no, he talks!"
        },
        {
            "start": 363690,
            "end": 367830,
            "text": "He does. I can talk. I love to talk."
        },
        {
            "start": 367900,
            "end": 370830,
            "text": "I\u0027m the talkingest\ndamn thing you ever saw."
        },
        {
            "start": 370900,
            "end": 373470,
            "text": "- Get her out of my sight.\n-No, no!"
        },
        {
            "start": 373570,
            "end": 377940,
            "text": "I swear! Oh! He can talk!"
        },
        {
            "start": 380280,
            "end": 382680,
            "text": "Hey! I can fly!"
        },
        {
            "start": 382780,
            "end": 384380,
            "text": "- He can fly!\n- He can fly!"
        },
        {
            "start": 384450,
            "end": 387520,
            "text": "- He can talk!\nHa, ha! That\u0027s right, fool!"
        },
        {
            "start": 387580,
            "end": 389890,
            "text": "Now I\u0027m a flying, talking donkey."
        },
        {
            "start": 389950,
            "end": 392560,
            "text": "Your right have seen a house fly\nmaybe even a super fly,"
        },
        {
            "start": 392660,
            "end": 395690,
            "text": "but I bet you ain\u0027t never seen a donkey fly."
        },
        {
            "start": 395760,
            "end": 397690,
            "text": "Ha, ha!"
        },
        {
            "start": 397790,
            "end": 399700,
            "text": "Uh-oh."
        },
        {
            "start": 402100,
            "end": 404070,
            "text": "Seize him!"
        },
        {
            "start": 406000,
            "end": 408840,
            "text": "After him!\nHe\u0027s getting away!"
        },
        {
            "start": 416650,
            "end": 419880,
            "text": "Get him! This way! Turn!"
        },
        {
            "start": 425090,
            "end": 427390,
            "text": "You there. Ogre!"
        },
        {
            "start": 427490,
            "end": 429790,
            "text": "Aye?"
        },
        {
            "start": 429830,
            "end": 433930,
            "text": "By the order of Lord Farquaad, I am\nauthorized to place you under arrest..."
        },
        {
            "start": 434000,
            "end": 439600,
            "text": "and transport you to\na designated... resettlement facility."
        },
        {
            "start": 439700,
            "end": 443740,
            "text": "Oh, really?\nYou and what army?"
        },
        {
            "start": 454480,
            "end": 456490,
            "text": "Can I say something to you?"
        },
        {
            "start": 456590,
            "end": 459920,
            "text": "Listen, you was really, really somethin\u0027\nback there. Incredible!"
        },
        {
            "start": 460020,
            "end": 465760,
            "text": "Are you talkin\u0027 to-\nme? Whoa!"
        },
        {
            "start": 465830,
            "end": 469800,
            "text": "Yes, I was talkin\u0027 to you. Can I tell you\nthat you was great back there? Those guards!"
        },
        {
            "start": 469870,
            "end": 472600,
            "text": "They thought they was all of that.\nThen you showed up, and bam!"
        },
        {
            "start": 472700,
            "end": 474640,
            "text": "They was trippin\u0027 over themselves\nlike babes in the woods."
        },
        {
            "start": 474700,
            "end": 476640,
            "text": "That really made me\nfeel good to see that."
        },
        {
            "start": 476740,
            "end": 479740,
            "text": "- Oh, that\u0027s great. Really.\n- Man, it\u0027s good to be free."
        },
        {
            "start": 479880,
            "end": 485410,
            "text": "Now, why don\u0027t you go celebrate your\nfreedom with your own friends? Hmm?"
        },
        {
            "start": 485480,
            "end": 488820,
            "text": "But, uh, I don\u0027t have any friends."
        },
        {
            "start": 488850,
            "end": 491550,
            "text": "And I\u0027m not goin\u0027 out there by myself."
        },
        {
            "start": 491650,
            "end": 494160,
            "text": "Hey, wait a minute! I got\na great idea! I\u0027ll stick with you."
        },
        {
            "start": 494220,
            "end": 496160,
            "text": "You\u0027re a mean, green, fightin\u0027 machine."
        },
        {
            "start": 496260,
            "end": 498460,
            "text": "Together we\u0027ll scare\nthe spit out of anybody that crosses us."
        },
        {
            "start": 502870,
            "end": 505500,
            "text": "Oh, wow! That was really scary."
        },
        {
            "start": 505600,
            "end": 509310,
            "text": "If you don\u0027t mind me sayin\u0027 if that don(t work,\nyour breath certainly will get the job done,"
        },
        {
            "start": 509410,
            "end": 512540,
            "text": "\u0027cause you definitely need some Tic Tacs\nor something, \u0027cause your breath stinks!"
        },
        {
            "start": 512610,
            "end": 515810,
            "text": "You almost burned the hair outta my nose,"
        },
        {
            "start": 515910,
            "end": 518710,
            "text": "just like the time-"
        },
        {
            "start": 518810,
            "end": 522520,
            "text": "Then I ate some rotten berries. I had\nstrong gases eking out of my butt that day."
        },
        {
            "start": 522620,
            "end": 525320,
            "text": "Why are you following me?"
        },
        {
            "start": 526490,
            "end": 528990,
            "text": "I\u0027ll tell you why."
        },
        {
            "start": 529090,
            "end": 532030,
            "text": "\u0027Cause I\u0027m all alone"
        },
        {
            "start": 532090,
            "end": 535800,
            "text": "There\u0027s no one here beside me"
        },
        {
            "start": 535900,
            "end": 539600,
            "text": "My problems have all gone"
        },
        {
            "start": 539700,
            "end": 543010,
            "text": "There\u0027s no one to deride me"
        },
        {
            "start": 543070,
            "end": 545040,
            "text": "But you gotta have friend-"
        },
        {
            "start": 545140,
            "end": 548140,
            "text": "Stop singing!"
        },
        {
            "start": 548280,
            "end": 550310,
            "text": "It\u0027s no wonder\nyou don\u0027t have any friends."
        },
        {
            "start": 550380,
            "end": 554050,
            "text": "Wow. Only a true friend\nwould be that truly honest."
        },
        {
            "start": 554120,
            "end": 558190,
            "text": "Listen, little donkey.\nTake a look at me. What am I?"
        },
        {
            "start": 559220,
            "end": 561160,
            "text": "Uh-"
        },
        {
            "start": 563090,
            "end": 566330,
            "text": "-Really tall?\n- No! I\u0027m an ogre."
        },
        {
            "start": 566400,
            "end": 569630,
            "text": "You know.\n\"Grab your torch and pitchforks. \""
        },
        {
            "start": 569700,
            "end": 571700,
            "text": "Doesn\u0027t that bother you?"
        },
        {
            "start": 571770,
            "end": 573940,
            "text": "Nope."
        },
        {
            "start": 574000,
            "end": 576910,
            "text": "- Really?\n- Really,really."
        },
        {
            "start": 576970,
            "end": 580110,
            "text": "- Oh.\n- Man, I like you. What\u0027s your name?"
        },
        {
            "start": 580210,
            "end": 583810,
            "text": "Uh, Shrek."
        },
        {
            "start": 583910,
            "end": 587180,
            "text": "Shrek? Well, you know\nwhat I like about you, Shrek?"
        },
        {
            "start": 587250,
            "end": 591290,
            "text": "You got a kind of I-don\u0027t-care-what-nobody-\nthinks-of-me thing."
        },
        {
            "start": 591390,
            "end": 595190,
            "text": "I like that.\nI respect that, Shrek. You alright."
        },
        {
            "start": 595260,
            "end": 599260,
            "text": "Whoo! Look at that. Who\u0027d want\nto live in a place like that?"
        },
        {
            "start": 599360,
            "end": 602570,
            "text": "That would be my home."
        },
        {
            "start": 602670,
            "end": 605530,
            "text": "Oh! And it is lovely!\nJust beautiful."
        },
        {
            "start": 605630,
            "end": 609440,
            "text": "You are quite a decorator. It\u0027s amazing\nwhat you\u0027ve done with such a modest budget."
        },
        {
            "start": 609510,
            "end": 614210,
            "text": "I like that boulder.\nThat is a nice boulder."
        },
        {
            "start": 618010,
            "end": 620680,
            "text": "I guess you don\u0027t\nentertain much, do you?"
        },
        {
            "start": 620820,
            "end": 622590,
            "text": "I like my privacy."
        },
        {
            "start": 622690,
            "end": 625290,
            "text": "You know, I do too.\nThat\u0027s another thing we have in comon."
        },
        {
            "start": 625320,
            "end": 627590,
            "text": "Like, I hate it when\nyou got somebody in your face."
        },
        {
            "start": 627660,
            "end": 631590,
            "text": "You\u0027re trying to give them a hint, and they\nwon\u0027t leave. There\u0027s that awkward silence."
        },
        {
            "start": 634730,
            "end": 637470,
            "text": "- Can I stay with you?\n- Uh, what?"
        },
        {
            "start": 637530,
            "end": 640100,
            "text": "Can I stay with you, please?"
        },
        {
            "start": 640170,
            "end": 642510,
            "text": "- Of course!\n- Really?"
        },
        {
            "start": 642570,
            "end": 644940,
            "text": "- No.\n- Please! I don\u0027t wanna go back there!"
        },
        {
            "start": 645010,
            "end": 648940,
            "text": "You don\u0027t know what it\u0027s like to be\nconsidered a freak. Well, maybe you do."
        },
        {
            "start": 649010,
            "end": 651610,
            "text": "But that\u0027s why we gotta stick together. You gotta let me stay!"
        },
        {
            "start": 651750,
            "end": 654850,
            "text": "- Please! Please!\n- Okay! Okay!"
        },
        {
            "start": 654880,
            "end": 657520,
            "text": "- But one night only.\n- Ah! Thank you!"
        },
        {
            "start": 657620,
            "end": 659960,
            "text": "- What are you- No! No!\n- This is gonna be fun!"
        },
        {
            "start": 659990,
            "end": 661960,
            "text": "We can stay up late, swappin\u0027 manly stories,"
        },
        {
            "start": 661990,
            "end": 664830,
            "text": "and in the mornin\u0027 I\u0027m makin\u0027 waffles."
        },
        {
            "start": 664930,
            "end": 668130,
            "text": "- Oh!\n- Where do, uh, I sleep?"
        },
        {
            "start": 668230,
            "end": 670000,
            "text": "Outside!"
        },
        {
            "start": 670070,
            "end": 673170,
            "text": "Oh,well,\nI guess that\u0027s cool."
        },
        {
            "start": 673200,
            "end": 675270,
            "text": "I mean, I don\u0027t know you,\nand you don\u0027t know me,"
        },
        {
            "start": 675400,
            "end": 677740,
            "text": "so I guess outside is best, you know."
        },
        {
            "start": 677870,
            "end": 680840,
            "text": "Here I go."
        },
        {
            "start": 683050,
            "end": 687120,
            "text": "Good night."
        },
        {
            "start": 687220,
            "end": 691220,
            "text": "I mean, I do like the outdoors.\nI\u0027m a donkey. I was born outside."
        },
        {
            "start": 691290,
            "end": 693960,
            "text": "I\u0027ll just be sitting by myself\noutside, I guess, you know."
        },
        {
            "start": 694020,
            "end": 696330,
            "text": "By myself, outside."
        },
        {
            "start": 696430,
            "end": 701200,
            "text": "I\u0027m all alone.\nThere\u0027s no one here beside me"
        },
        {
            "start": 765490,
            "end": 768100,
            "text": "I thought I told you to stay outside."
        },
        {
            "start": 768160,
            "end": 771870,
            "text": "- I am outside."
        },
        {
            "start": 777370,
            "end": 781140,
            "text": "Well, gents, it\u0027s a far cry from\nthe farm, but what choice do we have?"
        },
        {
            "start": 781240,
            "end": 783780,
            "text": "It\u0027s not home,\nbut it\u0027ll do just fine."
        },
        {
            "start": 783850,
            "end": 785750,
            "text": "What a lovely bed."
        },
        {
            "start": 785850,
            "end": 788180,
            "text": "- Got ya.\n- I found some cheese."
        },
        {
            "start": 788250,
            "end": 790750,
            "text": "- Ow!\n- Blah! Awful stuff."
        },
        {
            "start": 792490,
            "end": 795120,
            "text": "-Is that you, Gorder?\n- How did you know?"
        },
        {
            "start": 795190,
            "end": 798090,
            "text": "Enough!\nWhat are you doing in my house?"
        },
        {
            "start": 798190,
            "end": 800130,
            "text": "Hey!"
        },
        {
            "start": 800200,
            "end": 802800,
            "text": "- Oh!, no, no, no."
        },
        {
            "start": 802900,
            "end": 806500,
            "text": "- Dead broad off the table.\n- Where are we supposed to put her? The bed\u0027s taken."
        },
        {
            "start": 806600,
            "end": 809740,
            "text": "Huh?"
        },
        {
            "start": 809810,
            "end": 812480,
            "text": "What?"
        },
        {
            "start": 812540,
            "end": 817310,
            "text": "I live in a swamp. I put up signs.\nI\u0027m a terrifying ogre!"
        },
        {
            "start": 817380,
            "end": 819980,
            "text": "What do I have to do\nto get a little privacy?"
        },
        {
            "start": 820050,
            "end": 822520,
            "text": "-Aah!\n- Oh, no."
        },
        {
            "start": 822590,
            "end": 825290,
            "text": "Oh, no."
        },
        {
            "start": 825390,
            "end": 828990,
            "text": "- No! No!"
        },
        {
            "start": 829020,
            "end": 831990,
            "text": "- What?"
        },
        {
            "start": 832030,
            "end": 834000,
            "text": "- Quit it.\n- Don\u0027t push."
        },
        {
            "start": 838400,
            "end": 841300,
            "text": "What are you doing\nin my swamp?"
        },
        {
            "start": 841370,
            "end": 843810,
            "text": "Swamp! Swamp! Swamp!"
        },
        {
            "start": 843870,
            "end": 847110,
            "text": "- Oh, dear!"
        },
        {
            "start": 847180,
            "end": 848840,
            "text": "Whoa!"
        },
        {
            "start": 848910,
            "end": 852310,
            "text": "All right, get out of here.\nAll of you, move it!"
        },
        {
            "start": 852380,
            "end": 854780,
            "text": "Come on! Let\u0027s go!\nHapaya! Hapaya! Hey!"
        },
        {
            "start": 854880,
            "end": 856820,
            "text": "- Quickly. Come on!\n- No, no!"
        },
        {
            "start": 856890,
            "end": 859120,
            "text": "No, no.\nNot there. Not there."
        },
        {
            "start": 859220,
            "end": 862660,
            "text": "- Oh!"
        },
        {
            "start": 862790,
            "end": 865660,
            "text": "Hey, don\u0027t look at me. I didn\u0027t invite them."
        },
        {
            "start": 865730,
            "end": 867830,
            "text": "Oh, gosh, no one invited us."
        },
        {
            "start": 867900,
            "end": 870530,
            "text": "- What?\n- We were forced to come here."
        },
        {
            "start": 870600,
            "end": 872740,
            "text": "- By who?\n- Lord Farquaad."
        },
        {
            "start": 872800,
            "end": 877570,
            "text": "He huffed und he puffed und he...\nsigned an eviction notice."
        },
        {
            "start": 877640,
            "end": 880340,
            "text": "All right."
        },
        {
            "start": 880410,
            "end": 884710,
            "text": "Who knows where\nthis Farquaad guy is?"
        },
        {
            "start": 884780,
            "end": 887580,
            "text": "- Oh, I do. I know where he is."
        },
        {
            "start": 887650,
            "end": 891350,
            "text": "Does anyone else\nknow where to find him?"
        },
        {
            "start": 892860,
            "end": 895090,
            "text": "- Anyone at all?\n- Me! Me!"
        },
        {
            "start": 895160,
            "end": 897090,
            "text": "- Anyone?\n- Oh! Oh, pick me!"
        },
        {
            "start": 897190,
            "end": 899560,
            "text": "Oh, I know! I know! Me, me!"
        },
        {
            "start": 899660,
            "end": 902800,
            "text": "Okay, fine."
        },
        {
            "start": 902900,
            "end": 907400,
            "text": "Attention,\nall fairy tale things."
        },
        {
            "start": 907500,
            "end": 912210,
            "text": "Do not get comfortable.\nYour welcome is officially worn out."
        },
        {
            "start": 912310,
            "end": 916010,
            "text": "In fact, I\u0027m gonna see\nthis guy Farquaad right now..."
        },
        {
            "start": 916080,
            "end": 919220,
            "text": "and get you all off my land\nand back where you came from!"
        },
        {
            "start": 926760,
            "end": 929760,
            "text": "Oh! You!"
        },
        {
            "start": 929790,
            "end": 931730,
            "text": "You\u0027re comin\u0027 with me."
        },
        {
            "start": 931830,
            "end": 934130,
            "text": "All right, that\u0027s\nwhat I like to hear, man"
        },
        {
            "start": 934230,
            "end": 938000,
            "text": "Shrek and donkey, two stalwart friends,\noff on a whirlwind big-city adventure."
        },
        {
            "start": 938100,
            "end": 940100,
            "text": "I love it!"
        },
        {
            "start": 940200,
            "end": 943370,
            "text": "- On the road again"
        },
        {
            "start": 943470,
            "end": 945410,
            "text": "I can\u0027t wait to get\non the road again"
        },
        {
            "start": 945470,
            "end": 947410,
            "text": "What did I say\nabout singing?"
        },
        {
            "start": 947510,
            "end": 949410,
            "text": "- Can I whistle?\n- No."
        },
        {
            "start": 949480,
            "end": 952280,
            "text": "- Can I hum it?\n- All right, hum it."
        },
        {
            "start": 982080,
            "end": 985110,
            "text": "That\u0027s enough.\nHe\u0027s ready to talk."
        },
        {
            "start": 998060,
            "end": 1000530,
            "text": "Run, run, run,\nas fast as you can."
        },
        {
            "start": 1000600,
            "end": 1004100,
            "text": "You can\u0027t catch me.\nI\u0027m the gingerbread man!"
        },
        {
            "start": 1004200,
            "end": 1007840,
            "text": "- You\u0027re a monster.\n- I\u0027m not the monster here. You are."
        },
        {
            "start": 1007900,
            "end": 1012170,
            "text": "You and the rest of that fairy tale\ntrash, poisoning my perfect world."
        },
        {
            "start": 1012240,
            "end": 1014140,
            "text": "Now, tell me!\nWhere are the others?"
        },
        {
            "start": 1014280,
            "end": 1016780,
            "text": "- Eat me!"
        },
        {
            "start": 1016880,
            "end": 1019750,
            "text": "I\u0027ve tried to be fair\nto you creatures."
        },
        {
            "start": 1019850,
            "end": 1023390,
            "text": "Now my patience has reached its end!\nTell me or I\u0027ll-"
        },
        {
            "start": 1023490,
            "end": 1026260,
            "text": "No, no, not the buttons.\nNot my gumdrop buttons."
        },
        {
            "start": 1026320,
            "end": 1028360,
            "text": "All right then.\nWho\u0027s hiding them?"
        },
        {
            "start": 1028420,
            "end": 1033760,
            "text": "Okay, I\u0027ll tell you.\nDo you know the muffin man?"
        },
        {
            "start": 1033830,
            "end": 1036170,
            "text": "- The muffin man?\n- The muffin man."
        },
        {
            "start": 1036270,
            "end": 1039700,
            "text": "Yes, I know the muffin man,\nwho lives on Drury Lane?"
        },
        {
            "start": 1039740,
            "end": 1043170,
            "text": "Well, she\u0027s married\nto the muffin man."
        },
        {
            "start": 1043270,
            "end": 1045410,
            "text": "- The muffin man?\n- The muffin man!"
        },
        {
            "start": 1045470,
            "end": 1048510,
            "text": "- She\u0027s married to the muffin man."
        },
        {
            "start": 1048580,
            "end": 1050250,
            "text": "My lord! We found it."
        },
        {
            "start": 1050310,
            "end": 1053550,
            "text": "Then what are you waiting for?\nBring it in."
        },
        {
            "start": 1064930,
            "end": 1067400,
            "text": "- Oh!"
        },
        {
            "start": 1067500,
            "end": 1071830,
            "text": "- Magic mirror-\n- Don\u0027t tell him anything!"
        },
        {
            "start": 1071900,
            "end": 1074270,
            "text": "No!"
        },
        {
            "start": 1074340,
            "end": 1078110,
            "text": "- Evening."
        },
        {
            "start": 1078170,
            "end": 1080040,
            "text": "Mirror, mirror, on the wall."
        },
        {
            "start": 1080110,
            "end": 1083550,
            "text": "Is this not the most\nperfect kingdom of them all?"
        },
        {
            "start": 1083580,
            "end": 1086180,
            "text": "Well, technically\nyou\u0027re not a king."
        },
        {
            "start": 1086280,
            "end": 1089650,
            "text": "Uh, Thelonius."
        },
        {
            "start": 1089750,
            "end": 1093420,
            "text": "- You were saying?\n- What I mean is, you\u0027re not a king yet."
        },
        {
            "start": 1093490,
            "end": 1097060,
            "text": "But you can become one. All\nyou have to do is marry a princess."
        },
        {
            "start": 1097130,
            "end": 1100130,
            "text": "- Go on."
        },
        {
            "start": 1100230,
            "end": 1103900,
            "text": "So, just sit back\nand relax, my lord,"
        },
        {
            "start": 1103930,
            "end": 1108240,
            "text": "because it\u0027s time for you to\nmeet today\u0027s eligible bachelorettes."
        },
        {
            "start": 1108300,
            "end": 1111910,
            "text": "And here they are!"
        },
        {
            "start": 1112040,
            "end": 1116980,
            "text": "Bachelorette number one is a mentally\nabused shut-in from a kingdom far, far away."
        },
        {
            "start": 1117080,
            "end": 1119320,
            "text": "She likes sushi\nand hot tubbing anytime."
        },
        {
            "start": 1119380,
            "end": 1122850,
            "text": "Her hobbies include cooking\nand cleaning for her two evil sisters."
        },
        {
            "start": 1122920,
            "end": 1125290,
            "text": "Please welcome Cinderella."
        },
        {
            "start": 1125350,
            "end": 1129430,
            "text": "Bachelorette number two\\is a cape-wearing girl\nfrom the land of fancy."
        },
        {
            "start": 1129490,
            "end": 1132230,
            "text": "Although she lives with\nseven other men, she\u0027s not easy."
        },
        {
            "start": 1132330,
            "end": 1135770,
            "text": "Just kiss her dead, frozen lips\nand find out what a live wire she is."
        },
        {
            "start": 1135870,
            "end": 1139570,
            "text": "Come on.\nGive it up for Snow White!"
        },
        {
            "start": 1139700,
            "end": 1142340,
            "text": "And last,\nbut certainly not least,"
        },
        {
            "start": 1142440,
            "end": 1144510,
            "text": "bachelorette number three\nis a fiery redhead..."
        },
        {
            "start": 1144610,
            "end": 1147110,
            "text": "from a dragon-guarded castle\nsurrounded by hot boiling lava!"
        },
        {
            "start": 1147210,
            "end": 1149250,
            "text": "But don\u0027t let that cool you off."
        },
        {
            "start": 1149310,
            "end": 1152480,
            "text": "She\u0027s a loaded pistol who likes pina\ncoladas and getting caught in the rain."
        },
        {
            "start": 1152580,
            "end": 1156620,
            "text": "Yours for the rescuing, Princess Fiona!"
        },
        {
            "start": 1156720,
            "end": 1159160,
            "text": "So will it be\nbachelorette number one,"
        },
        {
            "start": 1159260,
            "end": 1162760,
            "text": "bachelorette number two or bachelorette number three?"
        },
        {
            "start": 1162860,
            "end": 1164990,
            "text": "- Two! Two!\n- Three! Three!"
        },
        {
            "start": 1165090,
            "end": 1166430,
            "text": "- Two! Two!\n- Three!"
        },
        {
            "start": 1166500,
            "end": 1168630,
            "text": "Three? One?\nThree?"
        },
        {
            "start": 1168730,
            "end": 1170800,
            "text": "Three!\nPick number three, my lord!"
        },
        {
            "start": 1170870,
            "end": 1174600,
            "text": "Okay, okay, uh, number three!"
        },
        {
            "start": 1174700,
            "end": 1179410,
            "text": "Lord Farquaad,\nyou\u0027ve chosen Princess Fiona."
        },
        {
            "start": 1179510,
            "end": 1183350,
            "text": "If you love pina coladas"
        },
        {
            "start": 1183380,
            "end": 1186720,
            "text": "- And getting caught in the rain\n- Princess Fiona."
        },
        {
            "start": 1186820,
            "end": 1188980,
            "text": "- If you\u0027re not into yoga"
        },
        {
            "start": 1189090,
            "end": 1191820,
            "text": "All I have to do is\njust find someone who can go-"
        },
        {
            "start": 1191920,
            "end": 1194160,
            "text": "But I probably should mention\nthe little thing that happens at night."
        },
        {
            "start": 1194260,
            "end": 1196160,
            "text": "- I\u0027ll do it.\n- Yes, but after sunset-"
        },
        {
            "start": 1196260,
            "end": 1200330,
            "text": "Silence! I will make\nthis Princess Fiona my queen,"
        },
        {
            "start": 1200430,
            "end": 1204600,
            "text": "and DuLoc will finally\nhave the perfect king!"
        },
        {
            "start": 1204670,
            "end": 1206870,
            "text": "Captain, assemble\nyour finet men."
        },
        {
            "start": 1206970,
            "end": 1210840,
            "text": "We\u0027re going to have\na tournament."
        },
        {
            "start": 1212540,
            "end": 1215540,
            "text": "But that\u0027s it. That\u0027st it\nright there. That\u0027s DuLoc."
        },
        {
            "start": 1215610,
            "end": 1217650,
            "text": "I told ya I\u0027d find it."
        },
        {
            "start": 1217750,
            "end": 1221850,
            "text": "So, that must be\nLord Farquaad\u0027s castle."
        },
        {
            "start": 1221980,
            "end": 1224050,
            "text": "Uh-huh. That\u0027s the place."
        },
        {
            "start": 1224090,
            "end": 1227520,
            "text": "Do you think maybe\nhe\u0027s compensating for something?"
        },
        {
            "start": 1231790,
            "end": 1233730,
            "text": "Hey, wait.\nWait up, Shrek."
        },
        {
            "start": 1233830,
            "end": 1236700,
            "text": "Hurry, darling.\nWe\u0027re late. Hurry."
        },
        {
            "start": 1238330,
            "end": 1241840,
            "text": "- Hey, you!"
        },
        {
            "start": 1241900,
            "end": 1244770,
            "text": "Wait a second.\nLook, I\u0027m not gonna eat ya."
        },
        {
            "start": 1244840,
            "end": 1248740,
            "text": "- I just- I just-"
        },
        {
            "start": 1279280,
            "end": 1280880,
            "text": "It\u0027s quiet."
        },
        {
            "start": 1283280,
            "end": 1285610,
            "text": "Too quiet."
        },
        {
            "start": 1288180,
            "end": 1292290,
            "text": "- WHere is everybody?\n- Hey, look at this!"
        },
        {
            "start": 1305670,
            "end": 1309140,
            "text": "Welcome to DuLoc such a perfect town"
        },
        {
            "start": 1309210,
            "end": 1312640,
            "text": "Here we have some rules\nLet us lay them down"
        },
        {
            "start": 1312680,
            "end": 1316610,
            "text": "Don\u0027t make waves, stay inline\nand we\u0027ll get along fine"
        },
        {
            "start": 1316680,
            "end": 1320280,
            "text": "DuLoc is a perfect place"
        },
        {
            "start": 1320350,
            "end": 1324890,
            "text": "Please keep off of the grass\nShine your shoes, wipe your... face"
        },
        {
            "start": 1324990,
            "end": 1328590,
            "text": "DuLoc is, DuLoc is"
        },
        {
            "start": 1328690,
            "end": 1332130,
            "text": "DuLoc is a perfect"
        },
        {
            "start": 1332190,
            "end": 1335630,
            "text": "- Place"
        },
        {
            "start": 1339400,
            "end": 1343610,
            "text": "Wow! Let\u0027s do that again!"
        },
        {
            "start": 1343670,
            "end": 1346340,
            "text": "No. No.\nNo, no, no! No."
        },
        {
            "start": 1348780,
            "end": 1351180,
            "text": "Brave knights."
        },
        {
            "start": 1351250,
            "end": 1355380,
            "text": "- You are the best and brightest\nin the land."
        },
        {
            "start": 1355420,
            "end": 1357850,
            "text": "Today one of you\nshall prove himself-"
        },
        {
            "start": 1357920,
            "end": 1360290,
            "text": "All right. You\u0027re going the right way\nfor a smacked bottom."
        },
        {
            "start": 1360360,
            "end": 1362290,
            "text": "- Sorry about that."
        },
        {
            "start": 1362390,
            "end": 1367900,
            "text": "That champion shall have the honour-\nno,no- the privilege..."
        },
        {
            "start": 1367960,
            "end": 1372000,
            "text": "to go forth and rescue\nthe lovely Princess Fiona..."
        },
        {
            "start": 1372100,
            "end": 1375440,
            "text": "from the fiery keep\nof the dragon"
        },
        {
            "start": 1375540,
            "end": 1379740,
            "text": "If for any reason\nthe winner is unsuccessful,"
        },
        {
            "start": 1379810,
            "end": 1382950,
            "text": "the fist runner-up\nwill take his place"
        },
        {
            "start": 1383050,
            "end": 1385880,
            "text": "and so on and so forth."
        },
        {
            "start": 1385980,
            "end": 1391320,
            "text": "Some of you may die, but it\u0027s\na sacrifice I am willing to make."
        },
        {
            "start": 1396490,
            "end": 1398930,
            "text": "Let the tournament begin!"
        },
        {
            "start": 1402600,
            "end": 1404530,
            "text": "- Oh!"
        },
        {
            "start": 1404630,
            "end": 1407900,
            "text": "- What is that?"
        },
        {
            "start": 1407970,
            "end": 1412510,
            "text": "- It\u0027s hideous!\n-Ah, that\u0027s not very nice."
        },
        {
            "start": 1412640,
            "end": 1415080,
            "text": "- It\u0027s just a donkey.\nHuh?"
        },
        {
            "start": 1415140,
            "end": 1418080,
            "text": "Indeed. Knights, new plan!"
        },
        {
            "start": 1418180,
            "end": 1422890,
            "text": "The one who kills the ogre will be\nnamed champion! Have at him!"
        },
        {
            "start": 1422950,
            "end": 1426890,
            "text": "- Get him!\n- Oh, hey! Now come on! Hang on now."
        },
        {
            "start": 1426960,
            "end": 1429260,
            "text": "Go ahead! Get him!"
        },
        {
            "start": 1429360,
            "end": 1433030,
            "text": "Can\u0027t we just settle this\nover a pint?"
        },
        {
            "start": 1433100,
            "end": 1434360,
            "text": "Kill the beast!"
        },
        {
            "start": 1434430,
            "end": 1438930,
            "text": "No? All right then."
        },
        {
            "start": 1438970,
            "end": 1441040,
            "text": "Come on!"
        },
        {
            "start": 1448880,
            "end": 1453250,
            "text": "I don\u0027t give a damn\nabout my reputation"
        },
        {
            "start": 1453380,
            "end": 1457390,
            "text": "-You\u0027ve living in the past\nIt\u0027s a new generation"
        },
        {
            "start": 1457450,
            "end": 1460290,
            "text": "she wants to do"
        },
        {
            "start": 1460320,
            "end": 1462590,
            "text": "And that\u0027s what\nI\u0027m gonna do"
        },
        {
            "start": 1462660,
            "end": 1466600,
            "text": "And I don\u0027t give a damn\nabout my bad reputation"
        },
        {
            "start": 1466660,
            "end": 1470470,
            "text": "Oh, no, no, no, no, no\nNot me"
        },
        {
            "start": 1470530,
            "end": 1471265,
            "text": "Me, me, me"
        },
        {
            "start": 1471300,
            "end": 1472965,
            "text": "- Hey, Shrek, tag me! Tag me!"
        },
        {
            "start": 1473000,
            "end": 1476710,
            "text": "And I don\u0027t give a damn\nabout my reputation"
        },
        {
            "start": 1476810,
            "end": 1479080,
            "text": "Never said I wanted\nto improve my station"
        },
        {
            "start": 1479140,
            "end": 1481680,
            "text": "Ah!"
        },
        {
            "start": 1481710,
            "end": 1483780,
            "text": "And I\u0027m always feelin\u0027 good\nwhen I\u0027m having fun"
        },
        {
            "start": 1483850,
            "end": 1485950,
            "text": "And I don\u0027t have\nto please no one"
        },
        {
            "start": 1486050,
            "end": 1487950,
            "text": "The chair!\nGive him the chair!"
        },
        {
            "start": 1488020,
            "end": 1490650,
            "text": "And I don\u0027t give damn\nabout my bad reputation"
        },
        {
            "start": 1490790,
            "end": 1493890,
            "text": "Oh, no, no, no, no, no\nNot me"
        },
        {
            "start": 1493960,
            "end": 1495420,
            "text": "Me, me, me"
        },
        {
            "start": 1495490,
            "end": 1498160,
            "text": "Oh, no, no, no, no"
        },
        {
            "start": 1498260,
            "end": 1501930,
            "text": "Not me, not me"
        },
        {
            "start": 1502030,
            "end": 1504470,
            "text": "-Not me"
        },
        {
            "start": 1507600,
            "end": 1512840,
            "text": "Oh, yeah! Ah! Ah!"
        },
        {
            "start": 1512910,
            "end": 1515680,
            "text": "Thank you! Thank you very much!"
        },
        {
            "start": 1515780,
            "end": 1518150,
            "text": "I\u0027m here till Thursday.\nTry the veal! Ha, ha!"
        },
        {
            "start": 1524620,
            "end": 1526560,
            "text": "Shall I give\nthe order, sir?"
        },
        {
            "start": 1526660,
            "end": 1530260,
            "text": "No, I have a better idea."
        },
        {
            "start": 1530330,
            "end": 1535500,
            "text": "People of DuLoc,\nI give you our champion!"
        },
        {
            "start": 1535630,
            "end": 1539470,
            "text": "- What?\n- Congratulations, ogre."
        },
        {
            "start": 1539570,
            "end": 1543870,
            "text": "You\u0027ve won the honour of\nembarking on a great and noble quest."
        },
        {
            "start": 1543970,
            "end": 1548340,
            "text": "Quest? I\u0027m already on a quest,\na quest to get my swamp back."
        },
        {
            "start": 1548410,
            "end": 1551650,
            "text": "- Your swamp?\n- Yeah, my swamp!"
        },
        {
            "start": 1551750,
            "end": 1554180,
            "text": "Where you dumped\nthose fairy tale creatures!"
        },
        {
            "start": 1554250,
            "end": 1556890,
            "text": "- Indeed."
        },
        {
            "start": 1556990,
            "end": 1559050,
            "text": "All right, ogre,\nI\u0027ll make you a deal."
        },
        {
            "start": 1559090,
            "end": 1562930,
            "text": "Go on this quest for me, and\nI\u0027ll give you your swamp back."
        },
        {
            "start": 1562990,
            "end": 1565730,
            "text": "Exactly the way it was?"
        },
        {
            "start": 1565800,
            "end": 1568530,
            "text": "Down to the last\nslime-covered toadtool."
        },
        {
            "start": 1568630,
            "end": 1571870,
            "text": "- And the squatters?\n- As good as gone."
        },
        {
            "start": 1574940,
            "end": 1578910,
            "text": "What kind of quest?"
        },
        {
            "start": 1579240,
            "end": 1581540,
            "text": "Let me get this straight.\nYou\u0027re gonna go fight a dragon..."
        },
        {
            "start": 1581610,
            "end": 1584280,
            "text": "and rescue a princess just so Farquaad\nwill give you back a swamp..."
        },
        {
            "start": 1584350,
            "end": 1587480,
            "text": "which you only don\u0027t have because he\nfilled it full of freaks in the first place."
        },
        {
            "start": 1587580,
            "end": 1591890,
            "text": "- Is that about right?\n- Maybe there\u0027s a good reason donkeys shouldn\u0027t talk."
        },
        {
            "start": 1591990,
            "end": 1595520,
            "text": "I don\u0027t get it. Why don\u0027t you just\npull some of that ogre stuff on him?"
        },
        {
            "start": 1595620,
            "end": 1597560,
            "text": "Throttle him,\nlay siege to his fortress,"
        },
        {
            "start": 1597630,
            "end": 1600430,
            "text": "grind his bones to make your bread,\nthe whole ogre trip."
        },
        {
            "start": 1600530,
            "end": 1602470,
            "text": "Oh, I know what."
        },
        {
            "start": 1602530,
            "end": 1604730,
            "text": "Maybe I could have\ndecapitated an entire village..."
        },
        {
            "start": 1604870,
            "end": 1606940,
            "text": "and put thier heads\non a pike,"
        },
        {
            "start": 1607000,
            "end": 1610670,
            "text": "gotten a knife, cut open thier\nspleen and drink their fluids."
        },
        {
            "start": 1610770,
            "end": 1612880,
            "text": "Does that sound\ngood to you?"
        },
        {
            "start": 1612980,
            "end": 1615610,
            "text": "Uh, no, not really, no."
        },
        {
            "start": 1615680,
            "end": 1619480,
            "text": "For your information, there\u0027s a lot\nmore to ogres than people think."
        },
        {
            "start": 1619550,
            "end": 1622280,
            "text": "- Example?\n- Example?"
        },
        {
            "start": 1622350,
            "end": 1628520,
            "text": "- Okay, um, ogres are like onions.\n- They stink?"
        },
        {
            "start": 1628590,
            "end": 1630890,
            "text": "- Yes- No! - They\nmake you cry? - No!"
        },
        {
            "start": 1630990,
            "end": 1634430,
            "text": "You leave them out in the sun, they get all\nbrown, start sproutin\u0027 little white hairs."
        },
        {
            "start": 1634460,
            "end": 1636470,
            "text": "No! Layers!"
        },
        {
            "start": 1636530,
            "end": 1638870,
            "text": "Onions have layers."
        },
        {
            "start": 1638970,
            "end": 1642300,
            "text": "Ogres have layers!\nOnions have layers."
        },
        {
            "start": 1642370,
            "end": 1644770,
            "text": "You gt it? We both have layers."
        },
        {
            "start": 1646680,
            "end": 1650280,
            "text": "Oh, you both have layers. Oh."
        },
        {
            "start": 1650380,
            "end": 1653780,
            "text": "You know, not everybody likes onions."
        },
        {
            "start": 1653820,
            "end": 1656490,
            "text": "Cake! Everybody loves cakes!\nCakes have layers."
        },
        {
            "start": 1656550,
            "end": 1660490,
            "text": "I don\u0027t care...\nwhat everyone likes."
        },
        {
            "start": 1660560,
            "end": 1664230,
            "text": "Ogres are not like cakes."
        },
        {
            "start": 1666100,
            "end": 1669470,
            "text": "You know what else\neverybody likes? Parfaits."
        },
        {
            "start": 1669570,
            "end": 1671470,
            "text": "Have you ever met a person, you,\nsay, \"Let\u0027s get some parfait,\""
        },
        {
            "start": 1671530,
            "end": 1673470,
            "text": "they say, \"No,\nI don\u0027t like no parfait\"?"
        },
        {
            "start": 1673570,
            "end": 1676310,
            "text": "- Parfaits are delicious.\n- No!"
        },
        {
            "start": 1676370,
            "end": 1679180,
            "text": "You dense, irritating,\nminiature beast of burden!"
        },
        {
            "start": 1679210,
            "end": 1682140,
            "text": "Ogres are like onions!\nEnd of the story."
        },
        {
            "start": 1682280,
            "end": 1684210,
            "text": "Bye-bye. See ya later."
        },
        {
            "start": 1688050,
            "end": 1692460,
            "text": "Parfaits may be the most delicious thing\non the whole damn planet."
        },
        {
            "start": 1692520,
            "end": 1694720,
            "text": "You know, I think\nI preferred your humming."
        },
        {
            "start": 1694820,
            "end": 1697490,
            "text": "Do you have a tissue or something?\nI\u0027m making a mess."
        },
        {
            "start": 1697560,
            "end": 1700530,
            "text": "Just the word parfait\nmake me start slobbering."
        },
        {
            "start": 1704900,
            "end": 1710810,
            "text": "I\u0027m on my way from misery\nto happiness today."
        },
        {
            "start": 1710870,
            "end": 1713410,
            "text": "Uh-huh, uh-huh\nUh-huh, uh-huh"
        },
        {
            "start": 1713480,
            "end": 1718980,
            "text": "I\u0027m on my way from misery\nto happiness today"
        },
        {
            "start": 1719010,
            "end": 1721480,
            "text": "Uh-huh, uh-huh\nUh-huh, uh-huh"
        },
        {
            "start": 1721580,
            "end": 1725490,
            "text": "And everything\nthat you receive up yonder"
        },
        {
            "start": 1725590,
            "end": 1729490,
            "text": "Is what you give to me\nthe day I wander"
        },
        {
            "start": 1729590,
            "end": 1732530,
            "text": "I\u0027m on my way"
        },
        {
            "start": 1733630,
            "end": 1736530,
            "text": "I\u0027m on my way"
        },
        {
            "start": 1737630,
            "end": 1739700,
            "text": "I\u0027m on my way"
        },
        {
            "start": 1739800,
            "end": 1742270,
            "text": "Ooh! Shrek!\nDid you do that?"
        },
        {
            "start": 1742340,
            "end": 1746140,
            "text": "You gotta warn somebody before you\njust crack one off. My mouth was open."
        },
        {
            "start": 1746240,
            "end": 1749950,
            "text": "Believe me, Donkey, if\nit was me, you\u0027d be dead."
        },
        {
            "start": 1750010,
            "end": 1752750,
            "text": "It\u0027s brimstone."
        },
        {
            "start": 1752820,
            "end": 1756020,
            "text": "- We must be getting close.\n- Yeah, right, brimstone."
        },
        {
            "start": 1756090,
            "end": 1760190,
            "text": "Don\u0027t be talking about it\u0027s the brimstone. I\nknow what I smell. It\u0027s wasn\u0027t no brimstone."
        },
        {
            "start": 1760260,
            "end": 1762220,
            "text": "It didn\u0027t come off\nno stone neither."
        },
        {
            "start": 1780240,
            "end": 1783580,
            "text": "Sure, it\u0027s big enough,\nbut look at the location."
        },
        {
            "start": 1786720,
            "end": 1791420,
            "text": "Uh, Shrek? Uh, remember when\nyou said ogres have layers?"
        },
        {
            "start": 1791520,
            "end": 1793420,
            "text": "Oh, aye."
        },
        {
            "start": 1793490,
            "end": 1797130,
            "text": "Well, I have a bit\nof a confession to make."
        },
        {
            "start": 1797190,
            "end": 1800560,
            "text": "Donkeys don\u0027t have layers. We wear\nour fear right out there on our sleeves."
        },
        {
            "start": 1800630,
            "end": 1804730,
            "text": "- Wait a second. Donkeys don\u0027t have sleeves.\n- You know what I mean."
        },
        {
            "start": 1804800,
            "end": 1807500,
            "text": "You can\u0027t tell me\nyou\u0027re afraid of heights."
        },
        {
            "start": 1807540,
            "end": 1812170,
            "text": "I\u0027m just a little uncomfortable about being on\na rickely bridge over a boiling lake of lava!"
        },
        {
            "start": 1812240,
            "end": 1816280,
            "text": "Come on, Donkey.\nI\u0027m right here beside ya, okay?"
        },
        {
            "start": 1816380,
            "end": 1819010,
            "text": "For emotional support,"
        },
        {
            "start": 1819120,
            "end": 1823690,
            "text": "we\u0027ll just tackle this thing\ntogether one little baby stap at a time."
        },
        {
            "start": 1823750,
            "end": 1825690,
            "text": "- Really?\n- Really, really."
        },
        {
            "start": 1825790,
            "end": 1828960,
            "text": "- Okay, that makes me feel so much better.\n- Just keep moving."
        },
        {
            "start": 1829060,
            "end": 1833630,
            "text": "- And don\u0027t look down. Okay,\ndon\u0027t look down. Don\u0027t look down."
        },
        {
            "start": 1833700,
            "end": 1837100,
            "text": "Don\u0027t look down. \\Keep on moving. Don\u0027t look down."
        },
        {
            "start": 1837170,
            "end": 1840970,
            "text": "Shrek! I\u0027m lookin\u0027 down!"
        },
        {
            "start": 1841100,
            "end": 1843970,
            "text": "Oh, God, I can\u0027t do this!\nJust let me off, please!"
        },
        {
            "start": 1844070,
            "end": 1847610,
            "text": "- But you\u0027re already halfway.\n- But I know that half is safe!"
        },
        {
            "start": 1847710,
            "end": 1850480,
            "text": "Okay, fine. I don\u0027t have time for this. You go back."
        },
        {
            "start": 1850550,
            "end": 1852980,
            "text": "- Shrek, no! Wait!\n- Just, Donkey-"
        },
        {
            "start": 1853080,
            "end": 1856190,
            "text": "- Let\u0027s have a dance then, shall we?\n- Don\u0027t do that!"
        },
        {
            "start": 1856250,
            "end": 1860120,
            "text": "Oh, I\u0027m sorry. Do what?"
        },
        {
            "start": 1860220,
            "end": 1863330,
            "text": "- Oh, this?\n- Yes, that!"
        },
        {
            "start": 1863390,
            "end": 1866760,
            "text": "Yes? Yes, do it. Okay."
        },
        {
            "start": 1866860,
            "end": 1869170,
            "text": "No Shrek!"
        },
        {
            "start": 1869270,
            "end": 1872400,
            "text": "- No! Stop it!\n- You said do it! I\u0027m doin\u0027 it."
        },
        {
            "start": 1872470,
            "end": 1876740,
            "text": "I\u0027m gonna die. I\u0027m gonna die.\nShrek, I\u0027m gonna die."
        },
        {
            "start": 1876770,
            "end": 1879280,
            "text": "Oh!"
        },
        {
            "start": 1879410,
            "end": 1883410,
            "text": "That\u0027ll do, Donkey.\nThat\u0027ll do."
        },
        {
            "start": 1886780,
            "end": 1888980,
            "text": "Cool."
        },
        {
            "start": 1889090,
            "end": 1891890,
            "text": "So where is this\nfire-breathing pain-in-the-neck anyway?"
        },
        {
            "start": 1891950,
            "end": 1895730,
            "text": "Inside, waiting for us\nto rescue her."
        },
        {
            "start": 1895790,
            "end": 1898690,
            "text": "I was talkin\u0027 about the dragon, Shrek."
        },
        {
            "start": 1914180,
            "end": 1916110,
            "text": "You afraid?"
        },
        {
            "start": 1916180,
            "end": 1919520,
            "text": "No, but- Shh."
        },
        {
            "start": 1919550,
            "end": 1923090,
            "text": "Oh, good. Me neither."
        },
        {
            "start": 1923190,
            "end": 1926590,
            "text": "\u0027Cause there\u0027s nothin\u0027\nwrong with bein\u0027 afraid."
        },
        {
            "start": 1926690,
            "end": 1929890,
            "text": "Fear\u0027s a sensible response\nto an unfamiliar situation."
        },
        {
            "start": 1929990,
            "end": 1932030,
            "text": "Unfamiliar dangerous\nsituation, I might add."
        },
        {
            "start": 1932090,
            "end": 1934760,
            "text": "With a dragon that breathes fire\nand eats knights and breathes fire,"
        },
        {
            "start": 1934800,
            "end": 1938300,
            "text": "it sure doesn\u0027t mean you\u0027re a coward if you\u0027re a little scared."
        },
        {
            "start": 1938370,
            "end": 1942370,
            "text": "I sure as heck ain\u0027t no coward.\nI know that."
        },
        {
            "start": 1942470,
            "end": 1945810,
            "text": "Donkey, two things, okay?"
        },
        {
            "start": 1945880,
            "end": 1948410,
            "text": "Shut... up."
        },
        {
            "start": 1948480,
            "end": 1951250,
            "text": "Now go over there and see\\if you can find any stairs."
        },
        {
            "start": 1951310,
            "end": 1953980,
            "text": "Stairs? I thought\nwe was lookin\u0027 for the princess."
        },
        {
            "start": 1954050,
            "end": 1958920,
            "text": "The princess will be up the stairs in\nthe highest room in the tallest tower."
        },
        {
            "start": 1959020,
            "end": 1962590,
            "text": "- What makes you think she\u0027ll be there?\n- I read it in a book once."
        },
        {
            "start": 1962690,
            "end": 1966600,
            "text": "Cool. You handle the dragon.\nI\u0027ll handle the stairs."
        },
        {
            "start": 1966700,
            "end": 1969700,
            "text": "I\u0027ll find those stairs.\nI\u0027ll whip their butt too."
        },
        {
            "start": 1969830,
            "end": 1971770,
            "text": "Those stairs won\u0027t know\nwhich way they\u0027re goin\u0027."
        },
        {
            "start": 1976310,
            "end": 1978240,
            "text": "I\u0027m gonna take drastic steps."
        },
        {
            "start": 1978340,
            "end": 1981110,
            "text": "Kick it to the kerb. Don\u0027t mess\nwith me. I\u0027m the stair master."
        },
        {
            "start": 1981210,
            "end": 1985780,
            "text": "I\u0027ve mastered the stairs. I wish I had\na step right here. I\u0027d step all over it."
        },
        {
            "start": 1988780,
            "end": 1992290,
            "text": "Well, at least we know\nwhere the princess is, but where\u0027s the-"
        },
        {
            "start": 1992350,
            "end": 1996660,
            "text": "Dragon!"
        },
        {
            "start": 2010210,
            "end": 2012480,
            "text": "Donkey, look out!"
        },
        {
            "start": 2024120,
            "end": 2026160,
            "text": "- Got ya!"
        },
        {
            "start": 2029390,
            "end": 2034600,
            "text": "Whoa! Whoa! Whoa!"
        },
        {
            "start": 2048610,
            "end": 2053220,
            "text": "Oh! Aah! Aah!"
        },
        {
            "start": 2061920,
            "end": 2065330,
            "text": "No. Oh, no. No!"
        },
        {
            "start": 2065390,
            "end": 2069030,
            "text": "- Oh, what large teeth you have."
        },
        {
            "start": 2069170,
            "end": 2072970,
            "text": "I mean, white, sparkling teeth. I know you\nprobably hear this all the time from your food,"
        },
        {
            "start": 2073040,
            "end": 2077170,
            "text": "but you must bleach, \u0027cause that\nis one dazzling smile you got there."
        },
        {
            "start": 2077310,
            "end": 2079780,
            "text": "Do I detect a hint\nof minty freshness?"
        },
        {
            "start": 2079840,
            "end": 2084610,
            "text": "And you know what else?\nYou\u0027re- you\u0027re a girl dragon!"
        },
        {
            "start": 2084680,
            "end": 2089190,
            "text": "Oh, sure! I mean,\nof course you\u0027re a girl dragon."
        },
        {
            "start": 2089250,
            "end": 2093060,
            "text": "You\u0027re just reeking\noff feminine beauty."
        },
        {
            "start": 2093160,
            "end": 2095490,
            "text": "What\u0027s the matter with you?\nYou got something in your eye?"
        },
        {
            "start": 2095590,
            "end": 2098390,
            "text": "Ooh. Oh. Oh."
        },
        {
            "start": 2098530,
            "end": 2100860,
            "text": "Man, I\u0027d really love to stay,\nbut, you know, I\u0027m, uh-"
        },
        {
            "start": 2101000,
            "end": 2104900,
            "text": "I\u0027m an asthmatic, and I don\u0027t know if it\u0027d\nwork out if you\u0027re gonna blow smoke rings."
        },
        {
            "start": 2105030,
            "end": 2106940,
            "text": "Shrek!"
        },
        {
            "start": 2107000,
            "end": 2110770,
            "text": "No! Shrek! Shrek!"
        },
        {
            "start": 2110870,
            "end": 2112780,
            "text": "Shrek!"
        },
        {
            "start": 2144010,
            "end": 2145470,
            "text": "Oh! Oh!"
        },
        {
            "start": 2145540,
            "end": 2147480,
            "text": "- WAke up!\n- What?"
        },
        {
            "start": 2147580,
            "end": 2149180,
            "text": "Are you Princess Fiona?"
        },
        {
            "start": 2149250,
            "end": 2154580,
            "text": "I am, awaiting a knight\nso bold as to rescue me."
        },
        {
            "start": 2154720,
            "end": 2156690,
            "text": "Oh, that\u0027s nice.\nNow let\u0027s go!"
        },
        {
            "start": 2156790,
            "end": 2158790,
            "text": "But wait, Sir Knight."
        },
        {
            "start": 2158890,
            "end": 2161120,
            "text": "This be-ith our first meeting."
        },
        {
            "start": 2161220,
            "end": 2164930,
            "text": "Should it not be\na wonderful, romantic moment?"
        },
        {
            "start": 2164990,
            "end": 2169370,
            "text": "- Yeah, sorry, lady. There\u0027s no time.\n- Hey, wait. What are you doing?"
        },
        {
            "start": 2169430,
            "end": 2171500,
            "text": "You should sweep me off my feet..."
        },
        {
            "start": 2171600,
            "end": 2174970,
            "text": "out yonder window and down a rope\nonto your valiant steed."
        },
        {
            "start": 2175100,
            "end": 2177870,
            "text": "You\u0027ve had a lot of time\nto plan this, haven\u0027t you?"
        },
        {
            "start": 2177940,
            "end": 2182210,
            "text": "Mm-hmm."
        },
        {
            "start": 2182240,
            "end": 2185180,
            "text": "But we have to\nsavour this moment!"
        },
        {
            "start": 2185250,
            "end": 2187980,
            "text": "You could recite\nan epic poem for me."
        },
        {
            "start": 2188120,
            "end": 2190550,
            "text": "A ballad? A sonnet!"
        },
        {
            "start": 2190620,
            "end": 2193890,
            "text": "- A limerick? Or something!\n- I don\u0027t think so."
        },
        {
            "start": 2193960,
            "end": 2198390,
            "text": "Can I at least know\nthe name of my champion?"
        },
        {
            "start": 2198460,
            "end": 2200400,
            "text": "Um, Shrek."
        },
        {
            "start": 2200500,
            "end": 2204170,
            "text": "Sir Shrek."
        },
        {
            "start": 2204230,
            "end": 2209000,
            "text": "I pray that you take this favour\nas a token of my gratitude."
        },
        {
            "start": 2210140,
            "end": 2212140,
            "text": "Thanks!"
        },
        {
            "start": 2216150,
            "end": 2217950,
            "text": "You didn\u0027t slay the dragon?"
        },
        {
            "start": 2218050,
            "end": 2220520,
            "text": "- It\u0027s on my to-do list. Now come on!"
        },
        {
            "start": 2220620,
            "end": 2223190,
            "text": "But this isn\u0027t right!"
        },
        {
            "start": 2223290,
            "end": 2225550,
            "text": "You were meant to charge in,\nsword drawn, banner flying."
        },
        {
            "start": 2225620,
            "end": 2227690,
            "text": "That\u0027s what all\nthe other knights did."
        },
        {
            "start": 2227790,
            "end": 2230090,
            "text": "Yeah, right before\nthey burst into flame."
        },
        {
            "start": 2230130,
            "end": 2233730,
            "text": "That\u0027s not the point. Oh!"
        },
        {
            "start": 2233800,
            "end": 2236230,
            "text": "Wait. Where are you going?\nThe exit\u0027s over there."
        },
        {
            "start": 2236330,
            "end": 2238370,
            "text": "Well, I have to save my ass."
        },
        {
            "start": 2238430,
            "end": 2241670,
            "text": "What kind of knight are you?"
        },
        {
            "start": 2241740,
            "end": 2244170,
            "text": "One of a kind."
        },
        {
            "start": 2244240,
            "end": 2246610,
            "text": "Slow down. Slow down, baby, please."
        },
        {
            "start": 2246740,
            "end": 2250950,
            "text": "I believe it\u0027s healthy to get to\nknow someone over a long period of time."
        },
        {
            "start": 2251010,
            "end": 2253580,
            "text": "Just call me old-fashioned."
        },
        {
            "start": 2253650,
            "end": 2257190,
            "text": "I don\u0027t want to rush into\na physical relationship."
        },
        {
            "start": 2257250,
            "end": 2260720,
            "text": "I\u0027m not that emotionally ready\nfor commitment of, uh, this-"
        },
        {
            "start": 2260790,
            "end": 2263130,
            "text": "Magnitude really is\nthe word I\u0027m looking for."
        },
        {
            "start": 2263190,
            "end": 2266730,
            "text": "Magnitude- Hey, that is\nunwanted physical contact."
        },
        {
            "start": 2266760,
            "end": 2269230,
            "text": "Hey, what are you doing?"
        },
        {
            "start": 2269300,
            "end": 2272570,
            "text": "Okay, okay. Let\u0027s just back up a little\nand take this one step at a time."
        },
        {
            "start": 2272670,
            "end": 2276810,
            "text": "We really should get to know each other\nfirst as friends or pen pals."
        },
        {
            "start": 2276910,
            "end": 2281080,
            "text": "I\u0027m on the road a lot, but I just love receiving cards-"
        },
        {
            "start": 2281180,
            "end": 2283580,
            "text": "I\u0027d really love\nto stay, but-"
        },
        {
            "start": 2283680,
            "end": 2286250,
            "text": "Don\u0027t do that. That\u0027s my tail!\nThat\u0027s my personal tail."
        },
        {
            "start": 2286320,
            "end": 2289750,
            "text": "You\u0027re gonna tear it off. I don\u0027t give permission\n- What are you gonna do with that?"
        },
        {
            "start": 2289820,
            "end": 2293620,
            "text": "Hey, now. No way.\nNo! No!"
        },
        {
            "start": 2293720,
            "end": 2296460,
            "text": "No, no! No.\nNo, no, no! No! Oh!"
        },
        {
            "start": 2323350,
            "end": 2325290,
            "text": "- Hi, Princess!\n- It talks!"
        },
        {
            "start": 2325390,
            "end": 2329130,
            "text": "Yeah, it\u0027s getting him\nto shut up that\u0027s the trick."
        },
        {
            "start": 2334400,
            "end": 2336030,
            "text": "Oh!"
        },
        {
            "start": 2364690,
            "end": 2366630,
            "text": "Okay, you two,\nhead for the exit!"
        },
        {
            "start": 2367730,
            "end": 2370300,
            "text": "I\u0027ll take care of the dragon."
        },
        {
            "start": 2423890,
            "end": 2428590,
            "text": "- You did it!"
        },
        {
            "start": 2428690,
            "end": 2431530,
            "text": "You rescued me!\nYou\u0027re amizing. You\u0027re-"
        },
        {
            "start": 2431660,
            "end": 2435360,
            "text": "You\u0027re wonderful. You\u0027re..."
        },
        {
            "start": 2435430,
            "end": 2437770,
            "text": "a little unorthodox I\u0027ll admit."
        },
        {
            "start": 2437830,
            "end": 2442740,
            "text": "But thy deed is great,\nand thine heart is pure."
        },
        {
            "start": 2442840,
            "end": 2446640,
            "text": "- I am eternally in your debt."
        },
        {
            "start": 2446710,
            "end": 2451050,
            "text": "And where would a brave knight be\nwithout his noble steed?"
        },
        {
            "start": 2451080,
            "end": 2455820,
            "text": "I hope you heard that. She called me\na noble steed. She think I\u0027m a steed."
        },
        {
            "start": 2455890,
            "end": 2459060,
            "text": "The battle is won."
        },
        {
            "start": 2459160,
            "end": 2461620,
            "text": "You may remove your helmet,\ngood Sir Knight."
        },
        {
            "start": 2461720,
            "end": 2464330,
            "text": "- Uh, no.\n- Why not?"
        },
        {
            "start": 2464390,
            "end": 2466560,
            "text": "I have helmet hair."
        },
        {
            "start": 2466700,
            "end": 2469870,
            "text": "Please. I would\u0027st look\nupon the face of my rescuer."
        },
        {
            "start": 2470000,
            "end": 2472570,
            "text": "No, no, you wouldn\u0027t- \u0027st."
        },
        {
            "start": 2472640,
            "end": 2475100,
            "text": "But how will you kiss me?"
        },
        {
            "start": 2475200,
            "end": 2478940,
            "text": "What? That wasn\u0027t\nin the job description."
        },
        {
            "start": 2479040,
            "end": 2481440,
            "text": "Maybe it\u0027s a perk."
        },
        {
            "start": 2481510,
            "end": 2485920,
            "text": "No. It\u0027s destiny.\nOh, you must know how it goes."
        },
        {
            "start": 2485980,
            "end": 2489790,
            "text": "A princess locked in a tower\nand beset by a dragon..."
        },
        {
            "start": 2489850,
            "end": 2492820,
            "text": "is rescue by a brave knight,"
        },
        {
            "start": 2492860,
            "end": 2496160,
            "text": "and then they share true love\u0027s first kiss."
        },
        {
            "start": 2496260,
            "end": 2498330,
            "text": "Hmm? With Shrek?\nYou think- Wait."
        },
        {
            "start": 2498460,
            "end": 2501600,
            "text": "Wait. You think Shrek is your true love?"
        },
        {
            "start": 2501700,
            "end": 2503700,
            "text": "Well, yes."
        },
        {
            "start": 2507740,
            "end": 2510170,
            "text": "You think that Shrek is your true love!"
        },
        {
            "start": 2510270,
            "end": 2512170,
            "text": "What is so funny?"
        },
        {
            "start": 2512240,
            "end": 2515340,
            "text": "Let\u0027s just say,\nI\u0027m not your type, okay?"
        },
        {
            "start": 2515410,
            "end": 2518950,
            "text": "Of course you are.\nYou\u0027re my rescuer."
        },
        {
            "start": 2519050,
            "end": 2521480,
            "text": "Now- Now remove your helmet."
        },
        {
            "start": 2521580,
            "end": 2523790,
            "text": "Look. I really don\u0027t think\nthis is a good idea."
        },
        {
            "start": 2523890,
            "end": 2525860,
            "text": "- Just take off the helmet.\n- I\u0027m not going to."
        },
        {
            "start": 2525960,
            "end": 2527120,
            "text": "- Take it off!\n- No!"
        },
        {
            "start": 2527220,
            "end": 2529290,
            "text": "- Now!\n- Okay!"
        },
        {
            "start": 2529360,
            "end": 2533860,
            "text": "Easy. As you command,\nYour Highness."
        },
        {
            "start": 2543910,
            "end": 2547380,
            "text": "You- You\u0027re a- an ogre."
        },
        {
            "start": 2547440,
            "end": 2551350,
            "text": "Oh, you were expecting\nPrince Charming."
        },
        {
            "start": 2551450,
            "end": 2553950,
            "text": "Well, yes, actually."
        },
        {
            "start": 2555890,
            "end": 2559090,
            "text": "Oh no. This is all wrong."
        },
        {
            "start": 2559190,
            "end": 2561490,
            "text": "You\u0027re not supposed\nto be an ogre."
        },
        {
            "start": 2561560,
            "end": 2566830,
            "text": "Princess, I was sent\nto rescue you by Lord Farquaad, okay?"
        },
        {
            "start": 2566930,
            "end": 2568830,
            "text": "He\u0027s the one\nwho wants to marry you."
        },
        {
            "start": 2568930,
            "end": 2570830,
            "text": "Then why didn\u0027t\nhe come to rescue me?"
        },
        {
            "start": 2570900,
            "end": 2575270,
            "text": "Good question. You should\nask him that when we get there."
        },
        {
            "start": 2575300,
            "end": 2578370,
            "text": "But I have to be rescued\nby my true love,"
        },
        {
            "start": 2578440,
            "end": 2581240,
            "text": "not by some ogre and his- his pet."
        },
        {
            "start": 2581310,
            "end": 2583380,
            "text": "Well so much for noble steed."
        },
        {
            "start": 2583450,
            "end": 2586050,
            "text": "Look princess. You\u0027re not making my job any easier."
        },
        {
            "start": 2586150,
            "end": 2589520,
            "text": "Well I\u0027m sorry, but your job is not my problem."
        },
        {
            "start": 2589590,
            "end": 2594860,
            "text": "You can tell Lord Farquaad\nthat if he wants to rescue me properly,"
        },
        {
            "start": 2594920,
            "end": 2597290,
            "text": "I\u0027ll be waiting for him\nright here."
        },
        {
            "start": 2597360,
            "end": 2602400,
            "text": "Hey, I\u0027m no ones messenger boy,\nall right? I\u0027m a delivery boy."
        },
        {
            "start": 2602500,
            "end": 2604770,
            "text": "You wouldn\u0027t dare."
        },
        {
            "start": 2604830,
            "end": 2606870,
            "text": "- Ya comin\u0027, Donkey?\n- Put me down!"
        },
        {
            "start": 2606900,
            "end": 2608870,
            "text": "Yeah, I\u0027m right behind ya."
        },
        {
            "start": 2608940,
            "end": 2612580,
            "text": "Put me down or you will\nsuffer the consequences!"
        },
        {
            "start": 2612640,
            "end": 2617110,
            "text": "This is not dignified!\nPut me down!"
        },
        {
            "start": 2617180,
            "end": 2620120,
            "text": "Okay, so here\u0027s another question."
        },
        {
            "start": 2620150,
            "end": 2623220,
            "text": "Say there\u0027s a woman that digs you, right,\nbut you don\u0027t really like her that way."
        },
        {
            "start": 2623290,
            "end": 2625690,
            "text": "How do you let her down real easy\nso her feelings aren\u0027t hurt,"
        },
        {
            "start": 2625790,
            "end": 2628260,
            "text": "but you don\u0027t get burned to a crisp and eaten?"
        },
        {
            "start": 2628320,
            "end": 2631090,
            "text": "You just tell her\nshe\u0027s not your true love."
        },
        {
            "start": 2631230,
            "end": 2633900,
            "text": "Everyone knows it what happens when you find your-"
        },
        {
            "start": 2634000,
            "end": 2636570,
            "text": "Hey!"
        },
        {
            "start": 2636630,
            "end": 2638930,
            "text": "The sooner we get to DuLoc the better."
        },
        {
            "start": 2639030,
            "end": 2641940,
            "text": "Oh, yeah. You\u0027re gonna love it there,\nPrincess. It\u0027s beautiful!"
        },
        {
            "start": 2642040,
            "end": 2645780,
            "text": "And what of my groom-to-be?\nLord Farquaad? What\u0027s he like?"
        },
        {
            "start": 2645840,
            "end": 2647980,
            "text": "Well, let me put it this way, Princess."
        },
        {
            "start": 2648080,
            "end": 2652080,
            "text": "Men of Farquaad\u0027s stature\nare in short supply."
        },
        {
            "start": 2654180,
            "end": 2658250,
            "text": "I don\u0027t know. There are those who\nthink little of him."
        },
        {
            "start": 2660390,
            "end": 2663260,
            "text": "Stop it. Stop it,\nboth of you."
        },
        {
            "start": 2663330,
            "end": 2668300,
            "text": "You know, you\u0027re just jealous that you can never\nmeasure up to a great ruler like lord Farquaad."
        },
        {
            "start": 2668360,
            "end": 2671300,
            "text": "Yeah. Well maybe you\u0027re right, Princess."
        },
        {
            "start": 2671400,
            "end": 2674700,
            "text": "But I\u0027d let you do the \"measuring\" when you see him tomorrow."
        },
        {
            "start": 2674770,
            "end": 2678970,
            "text": "Tomorrow?\nIt\u0027s take that long?"
        },
        {
            "start": 2679080,
            "end": 2683780,
            "text": "- Shouldn\u0027t we stop to make camp?\n- No, that\u0027ll take longer."
        },
        {
            "start": 2683850,
            "end": 2688320,
            "text": "- We can keep going.\n- But there are robbers in the woods."
        },
        {
            "start": 2688350,
            "end": 2691720,
            "text": "Whoa! Time out, Shrek!\nCamping\u0027s starting to sound good."
        },
        {
            "start": 2691790,
            "end": 2695890,
            "text": "Hey. Come on. I\u0027m scarier than anything\nwe\u0027re going to see in this forest."
        },
        {
            "start": 2695960,
            "end": 2698360,
            "text": "I need to find somewhere to camp now!"
        },
        {
            "start": 2705200,
            "end": 2709310,
            "text": "- Hey! over here.\n- Shrek, we can do better than that."
        },
        {
            "start": 2709370,
            "end": 2711970,
            "text": "Now, I don\u0027t think this is fit for a princess."
        },
        {
            "start": 2712040,
            "end": 2714840,
            "text": "No, no, it\u0027s perfect.\nIt just needs a few homey touches."
        },
        {
            "start": 2714910,
            "end": 2719880,
            "text": "- Homey touches? Like what?"
        },
        {
            "start": 2719920,
            "end": 2724190,
            "text": "A door? Well, gentlemen, I bid thee good night."
        },
        {
            "start": 2724290,
            "end": 2726660,
            "text": "Do you want me to come in and read you a bedtime story? \u0027cause I will."
        },
        {
            "start": 2726760,
            "end": 2729060,
            "text": "I said good night!"
        },
        {
            "start": 2731560,
            "end": 2733500,
            "text": "Shrek, what are you doing?"
        },
        {
            "start": 2733560,
            "end": 2736630,
            "text": "I just- You know- Oh, come on."
        },
        {
            "start": 2736700,
            "end": 2739300,
            "text": "I was just kidding."
        },
        {
            "start": 2744640,
            "end": 2748240,
            "text": "And, uh, that one, that\u0027s Throwback,"
        },
        {
            "start": 2748310,
            "end": 2751680,
            "text": "the only ogre to ever spit\nover three wheat fields."
        },
        {
            "start": 2751810,
            "end": 2754880,
            "text": "Right. Yeah."
        },
        {
            "start": 2754950,
            "end": 2757890,
            "text": "Hey, can you tell my future form these stars?"
        },
        {
            "start": 2757950,
            "end": 2761590,
            "text": "Well, the stars don\u0027t tell the future, Donkey.\nThey tell stories."
        },
        {
            "start": 2761690,
            "end": 2765230,
            "text": "Look, there\u0027s Bloodnut, the Flatulent."
        },
        {
            "start": 2765330,
            "end": 2768960,
            "text": "- You can guess what he is famous for.\n- All right. Now I know you\u0027re making this up."
        },
        {
            "start": 2769030,
            "end": 2771770,
            "text": "No. Look. There he is,"
        },
        {
            "start": 2771830,
            "end": 2774900,
            "text": "and there\u0027s the group of hunters\nrunning away from his stench."
        },
        {
            "start": 2774970,
            "end": 2777570,
            "text": "Man, there ain\u0027t nothin\u0027 but a bunch of little dots."
        },
        {
            "start": 2777670,
            "end": 2781580,
            "text": "You know donkey,\nsometimes things are more than they appear."
        },
        {
            "start": 2781680,
            "end": 2783850,
            "text": "Hmm?"
        },
        {
            "start": 2783950,
            "end": 2785980,
            "text": "Forget it."
        },
        {
            "start": 2788820,
            "end": 2792560,
            "text": "Hey, Shrek, what we gonna do when we get our swamp back, anyway?"
        },
        {
            "start": 2792660,
            "end": 2794420,
            "text": "Our swamp?"
        },
        {
            "start": 2794490,
            "end": 2796590,
            "text": "You know. When we\u0027re through rescuing the princess and all that stuff."
        },
        {
            "start": 2796690,
            "end": 2801400,
            "text": "We? Donkey, there is no \"we\". There\u0027s no \"our\"."
        },
        {
            "start": 2801530,
            "end": 2803770,
            "text": "There\u0027s just me and my swamp."
        },
        {
            "start": 2803830,
            "end": 2808140,
            "text": "And the first thing I\u0027m gonna do is build\na ten-foot wall around my land."
        },
        {
            "start": 2808200,
            "end": 2810670,
            "text": "You cut me deep, Shrek."
        },
        {
            "start": 2810770,
            "end": 2814110,
            "text": "You cut me real deep just now."
        },
        {
            "start": 2814210,
            "end": 2816150,
            "text": "You know what I think?"
        },
        {
            "start": 2816210,
            "end": 2819320,
            "text": "I think this whole wall thing\nis just a way to keep somebody out."
        },
        {
            "start": 2819450,
            "end": 2823620,
            "text": "- No, do ya think?\n- Are you hidin\u0027 something?"
        },
        {
            "start": 2823690,
            "end": 2825650,
            "text": "Never mind, Donkey."
        },
        {
            "start": 2825720,
            "end": 2829090,
            "text": "Oh, this is another one of those onion things, isn\u0027t it?"
        },
        {
            "start": 2829230,
            "end": 2832830,
            "text": "No, this is one of those drop-it and leave-it-alone things."
        },
        {
            "start": 2832900,
            "end": 2836670,
            "text": "- Why don\u0027t you want to talk about it?\n- Why do you want to talk about it?"
        },
        {
            "start": 2836730,
            "end": 2838500,
            "text": "- Oh, Why you block?\n- I\u0027m not blocking."
        },
        {
            "start": 2838600,
            "end": 2840870,
            "text": "- Oh yes you are.\n- Donkey, I\u0027m warning you."
        },
        {
            "start": 2840940,
            "end": 2844440,
            "text": "- Who are you trying to keep out? Just tell me that Shrek. Who?\n- Everyone! Okay?"
        },
        {
            "start": 2845710,
            "end": 2848180,
            "text": "Oh, now we\u0027re gettin\u0027 somewhere."
        },
        {
            "start": 2848210,
            "end": 2851510,
            "text": "Oh, for \u0027the love of Pete!"
        },
        {
            "start": 2851610,
            "end": 2854050,
            "text": "Hey, what\u0027s your problem Shrek?\nWhat do you got against the whole world anyway?"
        },
        {
            "start": 2854120,
            "end": 2856350,
            "text": "Look. I\u0027m not the one with the problem, okay?"
        },
        {
            "start": 2856390,
            "end": 2859390,
            "text": "It\u0027s the world that seems to have a problem with me."
        },
        {
            "start": 2859460,
            "end": 2863330,
            "text": "People take one look at me and go, \"Aah! Help! Run!"
        },
        {
            "start": 2863460,
            "end": 2867160,
            "text": "A big, stupid, ugly ogre!\""
        },
        {
            "start": 2867260,
            "end": 2871470,
            "text": "They judge me, before they even know me."
        },
        {
            "start": 2871600,
            "end": 2873500,
            "text": "That\u0027s why I\u0027m better off alone."
        },
        {
            "start": 2881410,
            "end": 2883610,
            "text": "You know what?"
        },
        {
            "start": 2883680,
            "end": 2889350,
            "text": "When we met, I didn\u0027t think you\u0027re was just a big, stupid, ugly ogre."
        },
        {
            "start": 2889420,
            "end": 2892350,
            "text": "Yeah, I know."
        },
        {
            "start": 2892460,
            "end": 2895860,
            "text": "So, uh, are there any donkeys up there?"
        },
        {
            "start": 2895990,
            "end": 2899330,
            "text": "Well, there\u0027s, um, Gabby,"
        },
        {
            "start": 2899400,
            "end": 2901730,
            "text": "the Small and Annoying."
        },
        {
            "start": 2901800,
            "end": 2906700,
            "text": "Okay, okay. I see it, now. The big shiny\none, right there. That\u0027s one there?"
        },
        {
            "start": 2906800,
            "end": 2909970,
            "text": "- That\u0027s the moon.\n- Oh, okay."
        },
        {
            "start": 2931330,
            "end": 2933760,
            "text": "Again. Show me again."
        },
        {
            "start": 2938200,
            "end": 2942270,
            "text": "Mirror, mirror, show her to me. Show me the princess."
        },
        {
            "start": 2942410,
            "end": 2946410,
            "text": "- Hmph."
        },
        {
            "start": 2946510,
            "end": 2951280,
            "text": "Ah. Perfect."
        },
        {
            "start": 3014610,
            "end": 3017480,
            "text": "Mmm, yeah, you know I like it like that."
        },
        {
            "start": 3017580,
            "end": 3021820,
            "text": "- Oh come on, baby. I said I like it.\n- Donkey, wake up."
        },
        {
            "start": 3021920,
            "end": 3024420,
            "text": "- Huh? What?\n- Wake up."
        },
        {
            "start": 3024520,
            "end": 3026490,
            "text": "- What?\n- Good morning."
        },
        {
            "start": 3026560,
            "end": 3029830,
            "text": "How do you like your eggs?"
        },
        {
            "start": 3029930,
            "end": 3033860,
            "text": "- Good morning, Princess!\n- What\u0027s all this about?"
        },
        {
            "start": 3033960,
            "end": 3037930,
            "text": "You know, we kind of got of to a bad start yesterday."
        },
        {
            "start": 3038000,
            "end": 3040000,
            "text": "I wanted to make it up to you."
        },
        {
            "start": 3040070,
            "end": 3044640,
            "text": "I mean, after all, you did rescue me."
        },
        {
            "start": 3044740,
            "end": 3047740,
            "text": "- Uh, thanks."
        },
        {
            "start": 3047780,
            "end": 3051610,
            "text": "Well, eat up. We\u0027ve got a big day ahead of us."
        },
        {
            "start": 3056050,
            "end": 3057990,
            "text": "- Shrek!"
        },
        {
            "start": 3058120,
            "end": 3060020,
            "text": "What? It\u0027s a compliment."
        },
        {
            "start": 3060060,
            "end": 3063060,
            "text": "Better out than in, I always say."
        },
        {
            "start": 3063090,
            "end": 3066860,
            "text": "- Well, it\u0027s no way to behave in front of a princess."
        },
        {
            "start": 3066960,
            "end": 3069600,
            "text": "- Thanks\n- She\u0027s as nasty as you are."
        },
        {
            "start": 3069670,
            "end": 3074500,
            "text": "You know, you\u0027re not exactly what I expected."
        },
        {
            "start": 3074570,
            "end": 3079680,
            "text": "Well, maybe you shouldn\u0027t judge people\nbefore you get to know them."
        },
        {
            "start": 3082450,
            "end": 3084750,
            "text": "La liberte! Hey!"
        },
        {
            "start": 3084850,
            "end": 3086780,
            "text": "Princess!"
        },
        {
            "start": 3086850,
            "end": 3089520,
            "text": "- What are you doing?"
        },
        {
            "start": 3089590,
            "end": 3092560,
            "text": "Be still, mon cherie, for I am your saviour!"
        },
        {
            "start": 3092590,
            "end": 3097060,
            "text": "And I am rescuing you from this green-"
        },
        {
            "start": 3097130,
            "end": 3099730,
            "text": "- beast.\n- Hey!"
        },
        {
            "start": 3099760,
            "end": 3102130,
            "text": "That\u0027s my princess! Go find your own!"
        },
        {
            "start": 3102200,
            "end": 3107240,
            "text": "Please, monster! Can\u0027t you see I\u0027m a little busy here?"
        },
        {
            "start": 3107340,
            "end": 3110940,
            "text": "Look, pal. I don\u0027t know who you think you are!"
        },
        {
            "start": 3110970,
            "end": 3113610,
            "text": "Oh, of course! How rude that was."
        },
        {
            "start": 3113680,
            "end": 3115750,
            "text": "Please, let me introduce myself."
        },
        {
            "start": 3115850,
            "end": 3118710,
            "text": "Oh, Merry Men!"
        },
        {
            "start": 3122850,
            "end": 3124790,
            "text": "Ta,dah,dah,dah,whoo"
        },
        {
            "start": 3124920,
            "end": 3126990,
            "text": "I steal from the rich and give to the needy"
        },
        {
            "start": 3127060,
            "end": 3129190,
            "text": "- He takes a wee percentage\n- But I\u0027m not greedy"
        },
        {
            "start": 3129260,
            "end": 3131260,
            "text": "I rescue pretty damsels\nMan, I\u0027m good"
        },
        {
            "start": 3131330,
            "end": 3134330,
            "text": "- What a guy, Monsieur Hood"
        },
        {
            "start": 3138030,
            "end": 3140270,
            "text": "I like an honest fight and a saucy little maid"
        },
        {
            "start": 3140400,
            "end": 3142570,
            "text": "- What he\u0027s basically saying is he likes to get-\n- Paid"
        },
        {
            "start": 3142670,
            "end": 3145310,
            "text": "- So\n- When an ogre in the bush grabs a lady by the tush"
        },
        {
            "start": 3145410,
            "end": 3147610,
            "text": "- That\u0027s bad\n- That\u0027s bad"
        },
        {
            "start": 3147740,
            "end": 3149980,
            "text": "When a beauty\u0027s with a beast it makes me awfully mad"
        },
        {
            "start": 3150050,
            "end": 3151980,
            "text": "He\u0027s mad\nHe\u0027s really, really mad"
        },
        {
            "start": 3152080,
            "end": 3153950,
            "text": "I\u0027ll take my blade and ram it through your heart"
        },
        {
            "start": 3154020,
            "end": 3158650,
            "text": "Keep your eyes on me, boys \u0027cause I\u0027m about to start"
        },
        {
            "start": 3164230,
            "end": 3166860,
            "text": "Man, that was annoying!"
        },
        {
            "start": 3166900,
            "end": 3168660,
            "text": "Oh, you little..."
        },
        {
            "start": 3195930,
            "end": 3198990,
            "text": "Um, shall we?"
        },
        {
            "start": 3200330,
            "end": 3203230,
            "text": "- Hold the phone."
        },
        {
            "start": 3203770,
            "end": 3206740,
            "text": "Oh! Whoa!, whoa, whoa. Hold on now."
        },
        {
            "start": 3206840,
            "end": 3208840,
            "text": "- Where did that come from?\n- What?"
        },
        {
            "start": 3208940,
            "end": 3211770,
            "text": "That! Back there. That was amazing!"
        },
        {
            "start": 3211870,
            "end": 3213810,
            "text": "Where did you learn that?"
        },
        {
            "start": 3213880,
            "end": 3217010,
            "text": "Well- When one lives alone,"
        },
        {
            "start": 3217110,
            "end": 3219880,
            "text": "uh, one has to learn these things in case there\u0027s a-"
        },
        {
            "start": 3219920,
            "end": 3222550,
            "text": "- There is an arrow in your butt!\n- What?"
        },
        {
            "start": 3222690,
            "end": 3226420,
            "text": "- What? Oh, would you look at that?\n- Oh, no. This is all my fault."
        },
        {
            "start": 3226520,
            "end": 3228460,
            "text": "- I\u0027m so sorry.\nWhy? What\u0027s wrong?"
        },
        {
            "start": 3228520,
            "end": 3231890,
            "text": "- Shrek\u0027s hurt.\n- Shrek\u0027s hurt. Shrek\u0027s hurt? Oh, no, Shrek\u0027s gonna die."
        },
        {
            "start": 3231960,
            "end": 3235770,
            "text": "- Donkey, I\u0027m okay.\nYou can\u0027t do this to me Shrek. I\u0027m too young for you to die."
        },
        {
            "start": 3235830,
            "end": 3237970,
            "text": "Keep your legs elevated. Turn your head and cough."
        },
        {
            "start": 3238030,
            "end": 3241040,
            "text": "- Does anyone know how the Heimlich?\n- Donkey! Calm down."
        },
        {
            "start": 3241140,
            "end": 3245780,
            "text": "If you want to help Shrek, run into the woods and find me a blue flower with red thorns."
        },
        {
            "start": 3245840,
            "end": 3249380,
            "text": "Blue flower, red thorns. Okay, I\u0027m on it. Blue flower, red thorns."
        },
        {
            "start": 3249480,
            "end": 3252980,
            "text": "Don\u0027t die Shrek. And if you see a long tunnel, stay away from the light!"
        },
        {
            "start": 3253080,
            "end": 3256250,
            "text": "- Donkey!\n- Oh, yeah. Right. Blue Flower, red thorns."
        },
        {
            "start": 3256320,
            "end": 3258590,
            "text": "Blue flower, red thorns.\n- What are the flowers for?"
        },
        {
            "start": 3258690,
            "end": 3260860,
            "text": "- For getting rid of the Donkey.\n- Ah."
        },
        {
            "start": 3260990,
            "end": 3263390,
            "text": "Now you hold still, and I\u0027ll yank this thing out."
        },
        {
            "start": 3263460,
            "end": 3265660,
            "text": "Ow! Hey! Easy with the yankin\u0027."
        },
        {
            "start": 3265700,
            "end": 3268400,
            "text": "- I\u0027m sorry, but it has to come out.\n- No, it\u0027s tender."
        },
        {
            "start": 3268460,
            "end": 3270200,
            "text": "Now, hold on."
        },
        {
            "start": 3270270,
            "end": 3272370,
            "text": "What you\u0027re doing here is the opposite of help.\n- Don\u0027t move."
        },
        {
            "start": 3272470,
            "end": 3275100,
            "text": "- Look, time out.\n- Would you-"
        },
        {
            "start": 3275200,
            "end": 3279680,
            "text": "Okay. What do you propose we do?"
        },
        {
            "start": 3279780,
            "end": 3282450,
            "text": "Blue flower, red thorns. Blue flower,\nred thorns. Blue flower, red thorns."
        },
        {
            "start": 3282550,
            "end": 3284950,
            "text": "This would be so much easier if I wasn\u0027t colour-blind!"
        },
        {
            "start": 3285010,
            "end": 3287080,
            "text": "- Blue flower, red thorns.\n- Ow!"
        },
        {
            "start": 3287180,
            "end": 3289620,
            "text": "Hold on, Shrek! I\u0027m comin\u0027!"
        },
        {
            "start": 3291120,
            "end": 3293190,
            "text": "Ow! Not good."
        },
        {
            "start": 3293290,
            "end": 3295860,
            "text": "- Okay. Okay, I can nearly see the head."
        },
        {
            "start": 3295960,
            "end": 3298560,
            "text": "- It\u0027s just about-\n- Ow! Ohh!"
        },
        {
            "start": 3300360,
            "end": 3303030,
            "text": "Ahem."
        },
        {
            "start": 3303100,
            "end": 3306440,
            "text": "Nothing happened. We were just, uh-"
        },
        {
            "start": 3306570,
            "end": 3309310,
            "text": "Look, if you wanted to be alone, all you had to do is ask."
        },
        {
            "start": 3309370,
            "end": 3312610,
            "text": "Oh, come on! That\u0027s the last thing on my mind."
        },
        {
            "start": 3312680,
            "end": 3316380,
            "text": "The princess here was just- Ugh!"
        },
        {
            "start": 3319580,
            "end": 3322250,
            "text": "- Ow!\n- Hey, what\u0027s that?"
        },
        {
            "start": 3322350,
            "end": 3325150,
            "text": "That\u0027s- Is that blood?"
        },
        {
            "start": 3341470,
            "end": 3345140,
            "text": "My beloved monster and me"
        },
        {
            "start": 3346380,
            "end": 3350910,
            "text": "We go everywhere together"
        },
        {
            "start": 3351010,
            "end": 3355680,
            "text": "Wearin\u0027 a raincoat that has four sleeves"
        },
        {
            "start": 3355790,
            "end": 3360420,
            "text": "- Gets us through all kinds of weather"
        },
        {
            "start": 3360490,
            "end": 3365730,
            "text": "She will always be the only thing"
        },
        {
            "start": 3365830,
            "end": 3370370,
            "text": "That comes between me and the awful sting"
        },
        {
            "start": 3370430,
            "end": 3375500,
            "text": "That comes from living in a world that\u0027s so damn mean"
        },
        {
            "start": 3384810,
            "end": 3388950,
            "text": "Oh, oh-oh-oh-oh"
        },
        {
            "start": 3403370,
            "end": 3405330,
            "text": "Hey!"
        },
        {
            "start": 3405400,
            "end": 3409570,
            "text": "- La-la, la-la, la-la-la-la"
        },
        {
            "start": 3409670,
            "end": 3413710,
            "text": "La-la, la-la, la-la"
        },
        {
            "start": 3415750,
            "end": 3417610,
            "text": "There it is, Princess."
        },
        {
            "start": 3417710,
            "end": 3420080,
            "text": "Your future awaits you."
        },
        {
            "start": 3420150,
            "end": 3422420,
            "text": "- That\u0027s Duloc?\n- Yeah, I know."
        },
        {
            "start": 3422490,
            "end": 3425150,
            "text": "You know, Shrek thinks Lord Farquaad\u0027s\ncompensating for something,"
        },
        {
            "start": 3425220,
            "end": 3427690,
            "text": "which I think means, he has a really- Ow!"
        },
        {
            "start": 3427790,
            "end": 3429730,
            "text": "Um, I, uh-"
        },
        {
            "start": 3429790,
            "end": 3432230,
            "text": "- I guess we better move on.\n- Sure."
        },
        {
            "start": 3432330,
            "end": 3434400,
            "text": "But Shrek?"
        },
        {
            "start": 3434530,
            "end": 3437270,
            "text": "- I\u0027m- I\u0027m worried about Donkey."
        },
        {
            "start": 3437370,
            "end": 3439770,
            "text": "- What?\n- I mean, look at him."
        },
        {
            "start": 3439800,
            "end": 3442870,
            "text": "- He doesn\u0027t look so good.\n- What are you talking about? I\u0027m fine."
        },
        {
            "start": 3442910,
            "end": 3447940,
            "text": "Well, that\u0027s what they always say, and the\nnext thing you know, you\u0027re on your back."
        },
        {
            "start": 3447980,
            "end": 3450450,
            "text": "- Dead.\n- You know, she\u0027s right."
        },
        {
            "start": 3450550,
            "end": 3453420,
            "text": "You look awful. Do you want to sit down?"
        },
        {
            "start": 3453520,
            "end": 3456190,
            "text": "- You know, I\u0027ll make you up some tea.\n- Well, I didn\u0027t want to say nothin\u0027,"
        },
        {
            "start": 3456290,
            "end": 3459090,
            "text": "but I\u0027ve got this twinge in my neck,\nand when I turn my head like this, look."
        },
        {
            "start": 3459160,
            "end": 3461060,
            "text": "- Ow! See?"
        },
        {
            "start": 3461160,
            "end": 3463730,
            "text": "- Who\u0027s hungry? I\u0027ll find us some dinner.\n- I\u0027ll get the firewood."
        },
        {
            "start": 3463830,
            "end": 3468330,
            "text": "Hey, where are you goin\u0027? Oh man, I can\u0027t feel my toes!"
        },
        {
            "start": 3468360,
            "end": 3470900,
            "text": "I don\u0027t have any toes!"
        },
        {
            "start": 3470970,
            "end": 3473340,
            "text": "I think I need a hug."
        },
        {
            "start": 3477470,
            "end": 3479710,
            "text": "Mmm."
        },
        {
            "start": 3479780,
            "end": 3483750,
            "text": "Mmm. This is good. This is really good."
        },
        {
            "start": 3485980,
            "end": 3488250,
            "text": "- What is this?\n- Uh, weedrat."
        },
        {
            "start": 3488280,
            "end": 3490390,
            "text": "Rotisserie style."
        },
        {
            "start": 3490520,
            "end": 3493020,
            "text": "No kidding."
        },
        {
            "start": 3493120,
            "end": 3495020,
            "text": "Well, this is delicious."
        },
        {
            "start": 3495090,
            "end": 3497190,
            "text": "Well, they\u0027re also great in stews."
        },
        {
            "start": 3497290,
            "end": 3501500,
            "text": "Now, I don\u0027t mean to brag,\nbut I make a mean wheedrat stew."
        },
        {
            "start": 3505270,
            "end": 3509400,
            "text": "I guess I\u0027ll be dining\na little differently tomorrow night."
        },
        {
            "start": 3509470,
            "end": 3514540,
            "text": "Maybe you can come visit me in the swamp sometime."
        },
        {
            "start": 3514640,
            "end": 3516780,
            "text": "I\u0027ll cook all kinds of stuff for you."
        },
        {
            "start": 3516880,
            "end": 3520780,
            "text": "Swamp toast soup, fish eye tartare- you name it."
        },
        {
            "start": 3521380,
            "end": 3524450,
            "text": "I\u0027d like that."
        },
        {
            "start": 3533060,
            "end": 3536700,
            "text": "Um, Princess?"
        },
        {
            "start": 3538000,
            "end": 3540165,
            "text": "- Yes, Shrek?"
        },
        {
            "start": 3542400,
            "end": 3544075,
            "text": "- I was wondering"
        },
        {
            "start": 3544705,
            "end": 3546880,
            "text": "Are, you-"
        },
        {
            "start": 3549680,
            "end": 3552450,
            "text": "Are you gonna eat that?"
        },
        {
            "start": 3559260,
            "end": 3563360,
            "text": "Man, isn\u0027t this romantic? Just look at that sunset.\n- Sunset?"
        },
        {
            "start": 3563430,
            "end": 3566000,
            "text": "Oh, no! I mean, it\u0027s late."
        },
        {
            "start": 3566060,
            "end": 3567930,
            "text": "- I-It\u0027s very late.\n- What?"
        },
        {
            "start": 3568000,
            "end": 3570930,
            "text": "Wait a minute. I see what\u0027s goin\u0027 on here."
        },
        {
            "start": 3571030,
            "end": 3574000,
            "text": "- You\u0027re afraid of the dark, aren\u0027t you?\n- Yes!"
        },
        {
            "start": 3574070,
            "end": 3578070,
            "text": "Yes, that\u0027s it. I\u0027m terrified. You know I\u0027d better go inside."
        },
        {
            "start": 3578170,
            "end": 3581480,
            "text": "But don\u0027t feel bad, Princess. I used to be afraid of the dark, too, until-"
        },
        {
            "start": 3581540,
            "end": 3584310,
            "text": "Hey, no, wait. I\u0027m still afraid of the dark."
        },
        {
            "start": 3584450,
            "end": 3587550,
            "text": "- Good night."
        },
        {
            "start": 3587620,
            "end": 3590690,
            "text": "Good night."
        },
        {
            "start": 3590750,
            "end": 3593820,
            "text": "- Ohh!"
        },
        {
            "start": 3596760,
            "end": 3598690,
            "text": "Oh, what are you talkin\u0027 about?"
        },
        {
            "start": 3598760,
            "end": 3601500,
            "text": "I don\u0027t even wanna hear it. Look, I\u0027m an animal, and I got instincts."
        },
        {
            "start": 3601600,
            "end": 3604000,
            "text": "I know that you two are diggin\u0027 on each other. I could feel it."
        },
        {
            "start": 3604100,
            "end": 3606900,
            "text": "Oh, you\u0027re crazy. I\u0027m just bringing her back to Farquaad."
        },
        {
            "start": 3607000,
            "end": 3610340,
            "text": "Oh, come on, Shrek. Wake up and smell the pheromones."
        },
        {
            "start": 3610410,
            "end": 3613080,
            "text": "- Just go in and tell her how you feel.\n- I-"
        },
        {
            "start": 3613180,
            "end": 3617180,
            "text": "There\u0027s nothing to tell. Besides, even if I did tell her that,"
        },
        {
            "start": 3617250,
            "end": 3619150,
            "text": "well you know-"
        },
        {
            "start": 3619250,
            "end": 3622790,
            "text": "and I\u0027m not sayin\u0027 that I do \u0027cause I don\u0027t-"
        },
        {
            "start": 3622890,
            "end": 3625250,
            "text": "she\u0027s a princess and I\u0027m-"
        },
        {
            "start": 3625350,
            "end": 3628060,
            "text": "An ogre?"
        },
        {
            "start": 3628120,
            "end": 3630760,
            "text": "Yeah. An ogre."
        },
        {
            "start": 3630890,
            "end": 3634930,
            "text": "- Hey, where are you goin\u0027?\n- To get... more firewood."
        },
        {
            "start": 3654550,
            "end": 3655820,
            "text": "Princess?"
        },
        {
            "start": 3655920,
            "end": 3658650,
            "text": "Princess Fiona?"
        },
        {
            "start": 3662930,
            "end": 3665730,
            "text": "Princess, where are you?"
        },
        {
            "start": 3671030,
            "end": 3673500,
            "text": "Princess?"
        },
        {
            "start": 3676570,
            "end": 3679310,
            "text": "It\u0027s very spooky in here. I ain\u0027t playing no games."
        },
        {
            "start": 3689550,
            "end": 3691990,
            "text": "- Aah! - Oh,\nno! - No, help!"
        },
        {
            "start": 3692050,
            "end": 3695120,
            "text": "- Shh! - Shrek! Shrek! Shrek!\n- No, it\u0027s okay. It\u0027s okay."
        },
        {
            "start": 3695260,
            "end": 3698090,
            "text": "- What did you do with the princess?\n-Donkey, shhh, I\u0027m the princess."
        },
        {
            "start": 3698190,
            "end": 3700930,
            "text": "- Aah!\n- It\u0027s me, in this body."
        },
        {
            "start": 3701030,
            "end": 3703600,
            "text": "Oh, my God! You ate the princess!"
        },
        {
            "start": 3703670,
            "end": 3705570,
            "text": "- Can you hear me? - Donkey!"
        },
        {
            "start": 3705670,
            "end": 3707970,
            "text": "- Listen, keep breathing! I\u0027ll get you out of there!\n- No!"
        },
        {
            "start": 3708040,
            "end": 3710540,
            "text": "- Shrek! Shrek! Shrek!\n- Shh."
        },
        {
            "start": 3710610,
            "end": 3712480,
            "text": "- Shrek!\n- This is me."
        },
        {
            "start": 3717580,
            "end": 3720480,
            "text": "Princess?"
        },
        {
            "start": 3720550,
            "end": 3724020,
            "text": "What happened to you? You\u0027re, uh, uh,"
        },
        {
            "start": 3724120,
            "end": 3726060,
            "text": "uh, different."
        },
        {
            "start": 3726090,
            "end": 3728360,
            "text": "- I\u0027m ugly, okay?\n- Well, yeah!"
        },
        {
            "start": 3728460,
            "end": 3731590,
            "text": "Was it something that you ate? \u0027Cause I told Shrek those rats was a bad idea."
        },
        {
            "start": 3731660,
            "end": 3734200,
            "text": "- You are what you eat, I said. Now-\n-No."
        },
        {
            "start": 3734260,
            "end": 3737670,
            "text": "I- I\u0027ve been this way as long as I can remember."
        },
        {
            "start": 3737730,
            "end": 3740200,
            "text": "What do you mean? Look, I ain\u0027t never seen you like this before."
        },
        {
            "start": 3740300,
            "end": 3743440,
            "text": "It only happens when the sun goes down."
        },
        {
            "start": 3744840,
            "end": 3748480,
            "text": "\"By night one way, by day another."
        },
        {
            "start": 3748540,
            "end": 3750580,
            "text": "\"This shall be the norm..."
        },
        {
            "start": 3750680,
            "end": 3753780,
            "text": "\"until you find true love\u0027s first kiss..."
        },
        {
            "start": 3753920,
            "end": 3758320,
            "text": "and then, take love\u0027s true form. \""
        },
        {
            "start": 3758420,
            "end": 3761920,
            "text": "Ah, that\u0027s beautiful. I didn\u0027t know you wrote poetry."
        },
        {
            "start": 3762060,
            "end": 3763960,
            "text": "It\u0027s a spell."
        },
        {
            "start": 3764030,
            "end": 3766630,
            "text": "When I was a little girl,"
        },
        {
            "start": 3766730,
            "end": 3769400,
            "text": "a witch cast a spell on me."
        },
        {
            "start": 3769470,
            "end": 3772000,
            "text": "Every night I become this."
        },
        {
            "start": 3772100,
            "end": 3775770,
            "text": "This horrible, ugly beast!"
        },
        {
            "start": 3778240,
            "end": 3783310,
            "text": "I was placed in a tower to await the day my true love would rescue me."
        },
        {
            "start": 3783380,
            "end": 3786020,
            "text": "That\u0027s why I have to marry lord Farquaad tomorrow..."
        },
        {
            "start": 3786080,
            "end": 3789450,
            "text": "before the sun sets and he sees me..."
        },
        {
            "start": 3790750,
            "end": 3793690,
            "text": "like this."
        },
        {
            "start": 3793760,
            "end": 3797060,
            "text": "All right, all right. Calm down. Look, it\u0027s not that bad."
        },
        {
            "start": 3797160,
            "end": 3801330,
            "text": "You\u0027re not that ugly. Well, I ain\u0027t gonna lie. You are ugly."
        },
        {
            "start": 3801400,
            "end": 3804400,
            "text": "But you only look like this at night. Shrek\u0027s ugly 24-7."
        },
        {
            "start": 3804500,
            "end": 3807840,
            "text": "But Donkey, I\u0027m a princess,"
        },
        {
            "start": 3807940,
            "end": 3811510,
            "text": "and this is not how a princess is meant to look."
        },
        {
            "start": 3811610,
            "end": 3815340,
            "text": "Princess, how \u0027bout if you don\u0027t marry Farquaad?"
        },
        {
            "start": 3815440,
            "end": 3817350,
            "text": "I have to."
        },
        {
            "start": 3817450,
            "end": 3820280,
            "text": "Only my true love\u0027s kiss can brake the spell."
        },
        {
            "start": 3820350,
            "end": 3823850,
            "text": "But you know, you\u0027re kind of an ogre,"
        },
        {
            "start": 3823990,
            "end": 3827390,
            "text": "and Shrek- well, you\u0027ve got a lot in common."
        },
        {
            "start": 3827490,
            "end": 3828960,
            "text": "Shrek?"
        },
        {
            "start": 3829060,
            "end": 3832930,
            "text": "Princess, I- Uh, how\u0027s it going, first of all?"
        },
        {
            "start": 3833000,
            "end": 3835800,
            "text": "Good? Um, good for me too."
        },
        {
            "start": 3835870,
            "end": 3837800,
            "text": "I\u0027m okay."
        },
        {
            "start": 3837900,
            "end": 3842770,
            "text": "I saw this flower and thought of you because it\u0027s pretty and-"
        },
        {
            "start": 3842840,
            "end": 3847110,
            "text": "well, I don\u0027t really like it, but I thought you may like it, \u0027cause you\u0027re pretty."
        },
        {
            "start": 3847180,
            "end": 3850350,
            "text": "But I like you anyway. I\u0027d- uh, uh-"
        },
        {
            "start": 3852550,
            "end": 3856090,
            "text": "I\u0027m in trouble. Okay, here we go."
        },
        {
            "start": 3856190,
            "end": 3858650,
            "text": "I can\u0027t just marry whoever I want."
        },
        {
            "start": 3858750,
            "end": 3861060,
            "text": "Take a good look at me, Donkey."
        },
        {
            "start": 3861160,
            "end": 3863090,
            "text": "I mean, really,"
        },
        {
            "start": 3863160,
            "end": 3865800,
            "text": "who could ever love a beast so hideous and hugly?"
        },
        {
            "start": 3865900,
            "end": 3868330,
            "text": "\"Princess\" and \"ugly\" don\u0027t go together."
        },
        {
            "start": 3868400,
            "end": 3870800,
            "text": "- That\u0027s why I can\u0027t stay here with Shrek."
        },
        {
            "start": 3870870,
            "end": 3875340,
            "text": "My only chance to live happily ever after is to marry my true love."
        },
        {
            "start": 3875400,
            "end": 3878810,
            "text": "- Don\u0027t you see, Donkey?"
        },
        {
            "start": 3878910,
            "end": 3882180,
            "text": "That\u0027s just how it has to be."
        },
        {
            "start": 3882280,
            "end": 3885350,
            "text": "It\u0027s the only way to break the spell."
        },
        {
            "start": 3885410,
            "end": 3888150,
            "text": "- You at least gotta tell Shrek the truth.\n- No!"
        },
        {
            "start": 3888220,
            "end": 3891150,
            "text": "You can\u0027t breathe the word. No one must ever know."
        },
        {
            "start": 3891250,
            "end": 3893390,
            "text": "What\u0027s the point of being able to talk if you gotta keep secrets?"
        },
        {
            "start": 3893460,
            "end": 3896590,
            "text": "Promise you won\u0027t tell. Promise!"
        },
        {
            "start": 3896690,
            "end": 3899800,
            "text": "All right, all right. I won\u0027t tell him. But you should."
        },
        {
            "start": 3899860,
            "end": 3902830,
            "text": "I just know, before this is over, I\u0027m gonna need a whole lot of serious therapy."
        },
        {
            "start": 3902970,
            "end": 3906470,
            "text": "- Look at my eye twitchin\u0027."
        },
        {
            "start": 3929190,
            "end": 3932900,
            "text": "I tell him, I tell him not."
        },
        {
            "start": 3933400,
            "end": 3937400,
            "text": "I tell him, I tell him not."
        },
        {
            "start": 3937470,
            "end": 3939370,
            "text": "I tell him."
        },
        {
            "start": 3939470,
            "end": 3941570,
            "text": "Shrek!"
        },
        {
            "start": 3941640,
            "end": 3944170,
            "text": "Shrek, there\u0027s something I want-"
        },
        {
            "start": 3964990,
            "end": 3967200,
            "text": "Shrek."
        },
        {
            "start": 3968600,
            "end": 3970670,
            "text": "- Are you all right?\n- Perfect!"
        },
        {
            "start": 3970770,
            "end": 3972700,
            "text": "Never been better."
        },
        {
            "start": 3972770,
            "end": 3976070,
            "text": "I- I don\u0027t- There\u0027s something I have to tell you."
        },
        {
            "start": 3976170,
            "end": 3978670,
            "text": "You don\u0027t have to tell me anything, Princess."
        },
        {
            "start": 3978810,
            "end": 3980880,
            "text": "- I heard enough last night.\n- You heard what I said?"
        },
        {
            "start": 3980940,
            "end": 3982880,
            "text": "Every word."
        },
        {
            "start": 3982980,
            "end": 3985110,
            "text": "I thought you\u0027d understand?"
        },
        {
            "start": 3985250,
            "end": 3987150,
            "text": "Oh, I understand!"
        },
        {
            "start": 3987220,
            "end": 3990790,
            "text": "Like you said, \"Who could love a hideous, ugly beast?\""
        },
        {
            "start": 3990850,
            "end": 3993590,
            "text": "But I thought that wouldn\u0027t matter to you."
        },
        {
            "start": 3993660,
            "end": 3995390,
            "text": "Yeah, well, it does."
        },
        {
            "start": 3997960,
            "end": 4000200,
            "text": "- Ah, right on time."
        },
        {
            "start": 4000260,
            "end": 4003170,
            "text": "Princess, I brought you a little something."
        },
        {
            "start": 4007570,
            "end": 4011210,
            "text": "What\u0027d I miss? What\u0027d I miss?"
        },
        {
            "start": 4011340,
            "end": 4013980,
            "text": "Who said that? Couldn\u0027t have been a donkey."
        },
        {
            "start": 4015310,
            "end": 4017780,
            "text": "Princess Fiona."
        },
        {
            "start": 4017910,
            "end": 4020580,
            "text": "As promised. Now hand it over."
        },
        {
            "start": 4020650,
            "end": 4024920,
            "text": "Very well, ogre. The deed to your swamp, cleared out as agreed."
        },
        {
            "start": 4025020,
            "end": 4029530,
            "text": "Take it and go before I change my mind."
        },
        {
            "start": 4029630,
            "end": 4032530,
            "text": "Forgive me, Princess, for startling you,"
        },
        {
            "start": 4032560,
            "end": 4034500,
            "text": "but you startled me,"
        },
        {
            "start": 4034600,
            "end": 4038230,
            "text": "For I\u0027ve never seen such a radiant beauty before."
        },
        {
            "start": 4038330,
            "end": 4041300,
            "text": "I am lord Farquaad."
        },
        {
            "start": 4041440,
            "end": 4045270,
            "text": "Lord Farquaad? Oh, no, no."
        },
        {
            "start": 4045340,
            "end": 4048310,
            "text": "- Forgive me, my lord, for I was just saying..."
        },
        {
            "start": 4048410,
            "end": 4053880,
            "text": "a short... farewell."
        },
        {
            "start": 4053950,
            "end": 4058620,
            "text": "That is so sweet. You don\u0027t have to raise good manners on the ogre."
        },
        {
            "start": 4058690,
            "end": 4060590,
            "text": "It\u0027s not like it has feelings."
        },
        {
            "start": 4060690,
            "end": 4064630,
            "text": "No. You\u0027re right. It doesn\u0027t."
        },
        {
            "start": 4064730,
            "end": 4068730,
            "text": "Princess Fiona, beautiful fair flawless Fiona."
        },
        {
            "start": 4068800,
            "end": 4071800,
            "text": "- I ask your hand in marriage."
        },
        {
            "start": 4071900,
            "end": 4076940,
            "text": "Will you be the perfect bride for the perfect groom?"
        },
        {
            "start": 4080240,
            "end": 4085450,
            "text": "Lord Farquaad, I accept. Nothing would make-"
        },
        {
            "start": 4085510,
            "end": 4088620,
            "text": "Excellent! I\u0027ll start the plans for tomorrow we wed!"
        },
        {
            "start": 4088750,
            "end": 4090650,
            "text": "No! I mean, uh,"
        },
        {
            "start": 4090720,
            "end": 4093120,
            "text": "Why wait?"
        },
        {
            "start": 4093220,
            "end": 4097060,
            "text": "Let\u0027s get married today before the sun sets."
        },
        {
            "start": 4097130,
            "end": 4099800,
            "text": "Oh, anxious, are we? You\u0027re right."
        },
        {
            "start": 4099900,
            "end": 4103800,
            "text": "The sooner, the better. There\u0027s so much to do!"
        },
        {
            "start": 4103900,
            "end": 4106240,
            "text": "There is the caterer, the cake, the band, the guest list."
        },
        {
            "start": 4106340,
            "end": 4109340,
            "text": "Captain, round up some guests."
        },
        {
            "start": 4109440,
            "end": 4112110,
            "text": "Fare-thee-well, ogre."
        },
        {
            "start": 4112210,
            "end": 4114840,
            "text": "Shrek, what are you doing? You let her get away."
        },
        {
            "start": 4114910,
            "end": 4117610,
            "text": "- Yeah? So what?\n- Shrek, there\u0027s something about her you don\u0027t know."
        },
        {
            "start": 4117680,
            "end": 4120120,
            "text": "Look, I talked to her last night. She\u0027s"
        },
        {
            "start": 4120180,
            "end": 4124220,
            "text": "Yeah I know you talked to her last night. You\u0027re great pals, aren\u0027t ya?"
        },
        {
            "start": 4124290,
            "end": 4128090,
            "text": "Now, if you two are such good friends, why didn\u0027t you follow her home?"
        },
        {
            "start": 4128160,
            "end": 4130130,
            "text": "Shrek, I- I wanna go with you."
        },
        {
            "start": 4130230,
            "end": 4133160,
            "text": "I told you, didn\u0027t I? You\u0027re not coming home with me."
        },
        {
            "start": 4133260,
            "end": 4137200,
            "text": "I live alone! My swamp! Me! Nobody else!"
        },
        {
            "start": 4137300,
            "end": 4139400,
            "text": "Understand? Nobody!"
        },
        {
            "start": 4139470,
            "end": 4144310,
            "text": "Especially useless, pathetic, annoying, talking donkeys!"
        },
        {
            "start": 4144410,
            "end": 4147580,
            "text": "- But. I thought\n- Yeah. You know what?"
        },
        {
            "start": 4147610,
            "end": 4149850,
            "text": "You thought wrong!"
        },
        {
            "start": 4151610,
            "end": 4153520,
            "text": "Shrek."
        },
        {
            "start": 4159290,
            "end": 4163290,
            "text": "I heard there was a secret chord"
        },
        {
            "start": 4163390,
            "end": 4166430,
            "text": "That David played and it pleased the Lord"
        },
        {
            "start": 4166530,
            "end": 4171370,
            "text": "But you don\u0027t really care for music, do ya"
        },
        {
            "start": 4173100,
            "end": 4176440,
            "text": "It goes like this the fourth, the fifth"
        },
        {
            "start": 4176540,
            "end": 4179070,
            "text": "The minor fall the major lift"
        },
        {
            "start": 4179210,
            "end": 4185820,
            "text": "The baffled king composing hallelujah"
        },
        {
            "start": 4185880,
            "end": 4189150,
            "text": "Hallelujah"
        },
        {
            "start": 4189250,
            "end": 4201500,
            "text": "Hallelujah"
        },
        {
            "start": 4203400,
            "end": 4206900,
            "text": "Baby, I\u0027ve been here before"
        },
        {
            "start": 4207000,
            "end": 4210140,
            "text": "I know this room I\u0027ve walked this floor"
        },
        {
            "start": 4210210,
            "end": 4215280,
            "text": "I used to live alone before I knew you"
        },
        {
            "start": 4216980,
            "end": 4220080,
            "text": "I\u0027ve seen your flag on the marble arch"
        },
        {
            "start": 4220150,
            "end": 4223520,
            "text": "But love is not a victory march"
        },
        {
            "start": 4223590,
            "end": 4228060,
            "text": "It\u0027s a cold and it\u0027s a broken hallelujah"
        },
        {
            "start": 4229660,
            "end": 4232760,
            "text": "Hallelujah"
        },
        {
            "start": 4232860,
            "end": 4235970,
            "text": "Hallelujah"
        },
        {
            "start": 4236070,
            "end": 4239100,
            "text": "Hallelujah"
        },
        {
            "start": 4239200,
            "end": 4244670,
            "text": "Hallelujah"
        },
        {
            "start": 4246410,
            "end": 4250280,
            "text": "And all I ever learned from love"
        },
        {
            "start": 4250380,
            "end": 4252750,
            "text": "Is how to shoot at someone"
        },
        {
            "start": 4252820,
            "end": 4255420,
            "text": "- Who outdrew you"
        },
        {
            "start": 4255480,
            "end": 4259220,
            "text": "And it\u0027s not a cry you can hear at night"
        },
        {
            "start": 4259290,
            "end": 4262360,
            "text": "It\u0027s not somebody who\u0027s seen the light"
        },
        {
            "start": 4262420,
            "end": 4269470,
            "text": "- It\u0027s a cold and it\u0027s a broken hallelujah"
        },
        {
            "start": 4269570,
            "end": 4272330,
            "text": "Hallelujah"
        },
        {
            "start": 4272440,
            "end": 4285680,
            "text": "Hallelujah"
        },
        {
            "start": 4294020,
            "end": 4295990,
            "text": "Donkey?"
        },
        {
            "start": 4296090,
            "end": 4298290,
            "text": "- What are you doing?"
        },
        {
            "start": 4298360,
            "end": 4301360,
            "text": "I would think, of all the people, you would recognize a wall when you see one."
        },
        {
            "start": 4301400,
            "end": 4303330,
            "text": "Well, yeah."
        },
        {
            "start": 4303430,
            "end": 4306770,
            "text": "But the wall\u0027s supposed to go around my swamp, not through it."
        },
        {
            "start": 4306870,
            "end": 4309670,
            "text": "It is around your half. See, that\u0027s your half, and this is my half."
        },
        {
            "start": 4309740,
            "end": 4312340,
            "text": "Oh, your half? Hmm."
        },
        {
            "start": 4312440,
            "end": 4314940,
            "text": "Yes, my half. I helped rescue the princess."
        },
        {
            "start": 4315040,
            "end": 4317310,
            "text": "I did half the work, I get half the booty."
        },
        {
            "start": 4317380,
            "end": 4320750,
            "text": "Now hand me that big old rock, the one that looks like your head."
        },
        {
            "start": 4320780,
            "end": 4323120,
            "text": "- Back off!\n- No, you back off."
        },
        {
            "start": 4323220,
            "end": 4325220,
            "text": "- This is my swamp!\n- Our swamp."
        },
        {
            "start": 4325320,
            "end": 4327220,
            "text": "- Let go, Donkey!\n- You let go."
        },
        {
            "start": 4327290,
            "end": 4329290,
            "text": "- Stubborn jackass!\n- Smelly ogre."
        },
        {
            "start": 4329360,
            "end": 4331590,
            "text": "Fine!"
        },
        {
            "start": 4331660,
            "end": 4334960,
            "text": "- Hey, hey, come back here. I\u0027m not through with you yet.\n- Well, I\u0027m through with you."
        },
        {
            "start": 4335060,
            "end": 4338300,
            "text": "Uh-uh. Well, you know, with you it\u0027s always, \"Me, me, me!\""
        },
        {
            "start": 4338400,
            "end": 4341000,
            "text": "Well, guess what! Now it\u0027s my turn!"
        },
        {
            "start": 4341070,
            "end": 4343170,
            "text": "So you just shut up and pay attention!"
        },
        {
            "start": 4343270,
            "end": 4345210,
            "text": "You are mean to me."
        },
        {
            "start": 4345340,
            "end": 4347840,
            "text": "You insult me and you don\u0027t appreciate anything that I do!"
        },
        {
            "start": 4347940,
            "end": 4351050,
            "text": "You\u0027re always pushing me around or pushing me away."
        },
        {
            "start": 4351110,
            "end": 4354920,
            "text": "Oh, yeah? Well, if I treated you so bad, how come you came back?"
        },
        {
            "start": 4354980,
            "end": 4358220,
            "text": "Because that\u0027s what friend do! They forgive each other!"
        },
        {
            "start": 4358350,
            "end": 4361490,
            "text": "Oh, yeah. You\u0027re right, Donkey."
        },
        {
            "start": 4361590,
            "end": 4365830,
            "text": "I forgive you... for stabbin\u0027 me in the back!"
        },
        {
            "start": 4365900,
            "end": 4371300,
            "text": "Ohh! You\u0027re so wrapped up in layers, onion boy, you\u0027re afraid of your own feelings."
        },
        {
            "start": 4371430,
            "end": 4374840,
            "text": "- Go away!\n- There you are, doing it again just like you did to Fiona."
        },
        {
            "start": 4374940,
            "end": 4377540,
            "text": "And all she ever do was like you, maybe even love you."
        },
        {
            "start": 4377610,
            "end": 4381740,
            "text": "Love me? She said I was ugly, a hideous creature."
        },
        {
            "start": 4381780,
            "end": 4383780,
            "text": "I heard the two of youtalking."
        },
        {
            "start": 4383850,
            "end": 4385750,
            "text": "She wasn\u0027t talking about you."
        },
        {
            "start": 4385850,
            "end": 4388150,
            "text": "She was talking about, uh, somebody else."
        },
        {
            "start": 4390720,
            "end": 4393090,
            "text": "She wasn\u0027t talking about me?"
        },
        {
            "start": 4393190,
            "end": 4395490,
            "text": "Well, then who was she talking about?"
        },
        {
            "start": 4395590,
            "end": 4398290,
            "text": "Uh-uh, no way. I ain\u0027t saying anything. You don\u0027t wanna listen to me."
        },
        {
            "start": 4398390,
            "end": 4400700,
            "text": "- Right? Right?\n- Donkey!"
        },
        {
            "start": 4400760,
            "end": 4403300,
            "text": "- No!\n- Okay, look."
        },
        {
            "start": 4403400,
            "end": 4405300,
            "text": "I\u0027m sorry, all right?"
        },
        {
            "start": 4406940,
            "end": 4408870,
            "text": "Hmph."
        },
        {
            "start": 4410210,
            "end": 4413840,
            "text": "I\u0027m sorry."
        },
        {
            "start": 4413910,
            "end": 4418180,
            "text": "I guess I am just a big, stupid, ugly ogre."
        },
        {
            "start": 4419350,
            "end": 4421350,
            "text": "Can you forgive me?"
        },
        {
            "start": 4423550,
            "end": 4425490,
            "text": "Hey, that\u0027s the friends are for, right?"
        },
        {
            "start": 4425550,
            "end": 4428190,
            "text": "Right. Friends?"
        },
        {
            "start": 4428290,
            "end": 4430230,
            "text": "Friends."
        },
        {
            "start": 4430290,
            "end": 4433260,
            "text": "So, um,"
        },
        {
            "start": 4433360,
            "end": 4435600,
            "text": "what did Fiona say about me?"
        },
        {
            "start": 4435660,
            "end": 4438800,
            "text": "What are you asking me for? Why don\u0027t you just go ask her?"
        },
        {
            "start": 4438870,
            "end": 4441900,
            "text": "The wedding! We\u0027ll never make it in time."
        },
        {
            "start": 4441940,
            "end": 4445170,
            "text": "Ha-ha-ha! Never fear, for where there\u0027s a will, there\u0027s a way."
        },
        {
            "start": 4445240,
            "end": 4447410,
            "text": "And I have a way."
        },
        {
            "start": 4451780,
            "end": 4454220,
            "text": "- Donkey?"
        },
        {
            "start": 4454280,
            "end": 4456690,
            "text": "I guess this is just my animal magnetism."
        },
        {
            "start": 4456790,
            "end": 4459960,
            "text": "Aw, come here, you."
        },
        {
            "start": 4460020,
            "end": 4462830,
            "text": "All right, all right. Don\u0027t get all slobbery. No one likes a kiss ass."
        },
        {
            "start": 4462930,
            "end": 4465430,
            "text": "All right, hop on and hold on tight."
        },
        {
            "start": 4465530,
            "end": 4469030,
            "text": "I hadn\u0027t have a chance to install seat belts yet."
        },
        {
            "start": 4475470,
            "end": 4477940,
            "text": "Whoo!"
        },
        {
            "start": 4500700,
            "end": 4503200,
            "text": "People of DuLoc,"
        },
        {
            "start": 4503330,
            "end": 4506000,
            "text": "we gather here today..."
        },
        {
            "start": 4506070,
            "end": 4508070,
            "text": "to bear witness..."
        },
        {
            "start": 4508200,
            "end": 4510140,
            "text": "- to the union...\n- Um-"
        },
        {
            "start": 4510170,
            "end": 4512680,
            "text": "- of our new king-\n- Excuse me."
        },
        {
            "start": 4512740,
            "end": 4515380,
            "text": "- Could we just skip ahead to the \"I do\u0027s\"?"
        },
        {
            "start": 4515480,
            "end": 4517410,
            "text": "Go on."
        },
        {
            "start": 4522450,
            "end": 4526090,
            "text": "Go ahead, have some fun. If we need you, I\u0027ll whisle. How about that?"
        },
        {
            "start": 4528720,
            "end": 4530630,
            "text": "Shrek, wait, wait! Wait a minute!"
        },
        {
            "start": 4530730,
            "end": 4533400,
            "text": "- You want to do this right, don\u0027t you?\n- What are you talking about?"
        },
        {
            "start": 4533500,
            "end": 4535400,
            "text": "There\u0027s a line, There\u0027s a line you gotta wait for."
        },
        {
            "start": 4535500,
            "end": 4537970,
            "text": "The preacher\u0027s gonna say, \"Speak now or forever hold your peace. \""
        },
        {
            "start": 4538070,
            "end": 4541800,
            "text": "- That\u0027s when you say, \"I object!\"\n- I don\u0027t have time for this!"
        },
        {
            "start": 4541900,
            "end": 4543640,
            "text": "Wait, wait. What are you doing? Listen to me!"
        },
        {
            "start": 4543710,
            "end": 4545610,
            "text": "- Look, you love this woman, don\u0027t you?\n- Yes."
        },
        {
            "start": 4545740,
            "end": 4546980,
            "text": "- You wanna hold her?\n- Yes."
        },
        {
            "start": 4547040,
            "end": 4549280,
            "text": "- Please her?\n- Yes!"
        },
        {
            "start": 4549380,
            "end": 4551980,
            "text": "Then you got to, got to try a little tenderness"
        },
        {
            "start": 4552050,
            "end": 4554850,
            "text": "- The chicks love that romantic crap!\n- All right! Cut it out."
        },
        {
            "start": 4554950,
            "end": 4558520,
            "text": "- When does this guy say the line?\n- We gotta check it out."
        },
        {
            "start": 4558620,
            "end": 4561790,
            "text": "- And so, by the power vested in me,"
        },
        {
            "start": 4561890,
            "end": 4565090,
            "text": "- What do you see?\n- The whole town\u0027s in there."
        },
        {
            "start": 4565190,
            "end": 4568330,
            "text": "- I know pronounce you husband and wife,\n- They\u0027re at the altar."
        },
        {
            "start": 4568400,
            "end": 4571000,
            "text": "- king and queen.\n- Mother Fletcher! He already said it."
        },
        {
            "start": 4571070,
            "end": 4573700,
            "text": "- Oh, for the love of Pete!"
        },
        {
            "start": 4574940,
            "end": 4577010,
            "text": "I object!"
        },
        {
            "start": 4577070,
            "end": 4579180,
            "text": "- Shrek?"
        },
        {
            "start": 4579240,
            "end": 4581180,
            "text": "Oh, now what does he want?"
        },
        {
            "start": 4584710,
            "end": 4587750,
            "text": "Hi, everyone. Havin\u0027 a good time, are ya?"
        },
        {
            "start": 4587850,
            "end": 4590090,
            "text": "I love DuLoc, first of all."
        },
        {
            "start": 4590150,
            "end": 4592660,
            "text": "- Very clean.\n- What are you doing here?"
        },
        {
            "start": 4592720,
            "end": 4595660,
            "text": "Really, it\u0027s rude enough being alive when no one wants you."
        },
        {
            "start": 4595720,
            "end": 4599060,
            "text": "- but showing up uninvited to a wedding-\n- Fiona!"
        },
        {
            "start": 4599160,
            "end": 4602160,
            "text": "- I need to talk to you.\n- Oh, now you wanna talk?"
        },
        {
            "start": 4602260,
            "end": 4605230,
            "text": "Well, it\u0027s a little late for that, so if you\u0027ll excuse me-"
        },
        {
            "start": 4605300,
            "end": 4607700,
            "text": "- But you can\u0027t marry him.\n- And why not?"
        },
        {
            "start": 4607770,
            "end": 4611670,
            "text": "Because- Because he\u0027s just marrying you so he can be king."
        },
        {
            "start": 4611740,
            "end": 4614110,
            "text": "Outrageous! Fiona, don\u0027t listen to him."
        },
        {
            "start": 4614180,
            "end": 4617810,
            "text": "- He\u0027s not your true love.\n- And what do you know about true love?"
        },
        {
            "start": 4617910,
            "end": 4620250,
            "text": "Well, I- Uh-"
        },
        {
            "start": 4620350,
            "end": 4623720,
            "text": "I mean-\n- Oh, this is precious."
        },
        {
            "start": 4623820,
            "end": 4628460,
            "text": "the ogre has fallen in love with the princess!"
        },
        {
            "start": 4628560,
            "end": 4630830,
            "text": "- Oh, good Lord."
        },
        {
            "start": 4630930,
            "end": 4632800,
            "text": "An ogre and a princess!"
        },
        {
            "start": 4638430,
            "end": 4640940,
            "text": "Shrek, is this true?"
        },
        {
            "start": 4642400,
            "end": 4644910,
            "text": "Who cares? It\u0027s preposterous!"
        },
        {
            "start": 4644970,
            "end": 4648610,
            "text": "Fiona, my love, we\u0027re but a kiss away for our \"happily ever after. \""
        },
        {
            "start": 4648710,
            "end": 4651310,
            "text": "Now kiss me! Mmmm!"
        },
        {
            "start": 4655380,
            "end": 4658720,
            "text": "\"By night one way, by day another. \""
        },
        {
            "start": 4660660,
            "end": 4663390,
            "text": "I wanted to show you before."
        },
        {
            "start": 4685980,
            "end": 4688520,
            "text": "Well, uh,"
        },
        {
            "start": 4688580,
            "end": 4690590,
            "text": "that explains a lot."
        },
        {
            "start": 4690650,
            "end": 4694360,
            "text": "Ugh! It\u0027s disgusting!"
        },
        {
            "start": 4694460,
            "end": 4696460,
            "text": "Guards! Guards!"
        },
        {
            "start": 4696560,
            "end": 4699730,
            "text": "I order you to get that out of my sight now! Get them!"
        },
        {
            "start": 4699800,
            "end": 4702570,
            "text": "- Get them both!\n- No, no!"
        },
        {
            "start": 4702670,
            "end": 4706470,
            "text": "This hocus-pocus alters nothing. This marriage is binding, and that makes me king!"
        },
        {
            "start": 4706540,
            "end": 4708800,
            "text": "- See? See?\n- No, let go of me! Shrek!"
        },
        {
            "start": 4708900,
            "end": 4710640,
            "text": "- No!\nDon\u0027t just stand there, you morons."
        },
        {
            "start": 4710710,
            "end": 4713240,
            "text": "Get out of my way! Fiona!"
        },
        {
            "start": 4715880,
            "end": 4718410,
            "text": "Arrgh!"
        },
        {
            "start": 4718510,
            "end": 4721480,
            "text": "I\u0027ll make you regret the day we met. I\u0027ll see you drawn and quartered!"
        },
        {
            "start": 4721580,
            "end": 4724490,
            "text": "- You\u0027ll beg for death to save you!\n- No! Shrek!"
        },
        {
            "start": 4724590,
            "end": 4728060,
            "text": "- And as for you, my wife.\n- Fiona!"
        },
        {
            "start": 4728190,
            "end": 4730860,
            "text": "I\u0027ll have you locked back in that tower for the rest of your days!"
        },
        {
            "start": 4730930,
            "end": 4732760,
            "text": "- I am king!"
        },
        {
            "start": 4732830,
            "end": 4736630,
            "text": "I will have order! I will have perfection! I will have-"
        },
        {
            "start": 4737970,
            "end": 4740970,
            "text": "Aaah!"
        },
        {
            "start": 4741070,
            "end": 4743840,
            "text": "- Aah!\n- All right. Nobody move."
        },
        {
            "start": 4743910,
            "end": 4745980,
            "text": "I got a dragon here, and I\u0027m not afraid to use it."
        },
        {
            "start": 4746110,
            "end": 4748680,
            "text": "- I\u0027m a donkey on the edge!"
        },
        {
            "start": 4753380,
            "end": 4755750,
            "text": "Celebrity marriages. They never last, do they?"
        },
        {
            "start": 4757950,
            "end": 4759760,
            "text": "Go ahead Shrek."
        },
        {
            "start": 4761160,
            "end": 4763460,
            "text": "Uh, Fiona?"
        },
        {
            "start": 4764590,
            "end": 4766800,
            "text": "Yes, Shrek?"
        },
        {
            "start": 4766860,
            "end": 4770900,
            "text": "I- I love you."
        },
        {
            "start": 4771000,
            "end": 4773270,
            "text": "Really?"
        },
        {
            "start": 4774340,
            "end": 4777340,
            "text": "Really, really."
        },
        {
            "start": 4777440,
            "end": 4779340,
            "text": "I love you too."
        },
        {
            "start": 4784810,
            "end": 4786480,
            "text": "Aawww!"
        },
        {
            "start": 4788380,
            "end": 4791620,
            "text": "\"Until you find true love\u0027s first kiss..."
        },
        {
            "start": 4791750,
            "end": 4795060,
            "text": "and then take love\u0027s true form. \""
        },
        {
            "start": 4818610,
            "end": 4821520,
            "text": "\"Take love\u0027s true form. Take love\u0027s true form. \""
        },
        {
            "start": 4827720,
            "end": 4830190,
            "text": "Fiona?"
        },
        {
            "start": 4832730,
            "end": 4835260,
            "text": "Fiona."
        },
        {
            "start": 4835360,
            "end": 4837930,
            "text": "Are you all right?"
        },
        {
            "start": 4838000,
            "end": 4839940,
            "text": "Well, yes."
        },
        {
            "start": 4840040,
            "end": 4843240,
            "text": "But I don\u0027t understand."
        },
        {
            "start": 4843340,
            "end": 4845470,
            "text": "I\u0027m supposed to be beautiful."
        },
        {
            "start": 4848180,
            "end": 4850410,
            "text": "But you are beautiful."
        },
        {
            "start": 4852980,
            "end": 4855420,
            "text": "I was hoping this would be a happy ending."
        },
        {
            "start": 4859220,
            "end": 4863630,
            "text": "I thought love was only true in fairy tales"
        },
        {
            "start": 4864600,
            "end": 4865155,
            "text": "Oy!"
        },
        {
            "start": 4865190,
            "end": 4868400,
            "text": "Meant for someone else but not for me"
        },
        {
            "start": 4871400,
            "end": 4874370,
            "text": "Love was out to get me"
        },
        {
            "start": 4874470,
            "end": 4877240,
            "text": "That\u0027s the way it seemed"
        },
        {
            "start": 4877340,
            "end": 4881810,
            "text": "Disappointment haunted all my dreams"
        },
        {
            "start": 4881840,
            "end": 4885150,
            "text": "And then I saw her face"
        },
        {
            "start": 4885210,
            "end": 4888050,
            "text": "Now I\u0027m a believer"
        },
        {
            "start": 4888120,
            "end": 4890690,
            "text": "And not a trace"
        },
        {
            "start": 4890750,
            "end": 4893520,
            "text": "Of doubt in my mind"
        },
        {
            "start": 4893590,
            "end": 4897660,
            "text": "- I\u0027m in love\n- Ooh-aah"
        },
        {
            "start": 4897730,
            "end": 4900160,
            "text": "I\u0027m a believer I couldn\u0027t leave her"
        },
        {
            "start": 4900260,
            "end": 4902430,
            "text": "If I tried"
        },
        {
            "start": 4902570,
            "end": 4906350,
            "text": "God bless us, every one."
        },
        {
            "start": 4906385,
            "end": 4910130,
            "text": "Come on, y\u0027all! Ha ha!"
        },
        {
            "start": 4910210,
            "end": 4913340,
            "text": "Now I\u0027m a believer"
        },
        {
            "start": 4913430,
            "end": 4916220,
            "text": "Not a trace"
        },
        {
            "start": 4916300,
            "end": 4919100,
            "text": "Of doubt in my mind"
        },
        {
            "start": 4919180,
            "end": 4922440,
            "text": "I\u0027m in love Ooh-ahh"
        },
        {
            "start": 4922520,
            "end": 4926770,
            "text": "I\u0027m a believer I couldn\u0027t leave her if I tried"
        },
        {
            "start": 4926860,
            "end": 4929730,
            "text": "- Ooh!\n- Uh!"
        },
        {
            "start": 4929820,
            "end": 4932400,
            "text": "Then I saw her face"
        },
        {
            "start": 4932490,
            "end": 4935450,
            "text": "Now I\u0027m a believer"
        },
        {
            "start": 4935530,
            "end": 4938330,
            "text": "Not a trace"
        },
        {
            "start": 4938410,
            "end": 4941660,
            "text": "Of doubt in my mind"
        },
        {
            "start": 4941750,
            "end": 4946290,
            "text": "I\u0027m in love I\u0027m a believer"
        },
        {
            "start": 4946380,
            "end": 4948210,
            "text": "Come on!"
        },
        {
            "start": 4948290,
            "end": 4950210,
            "text": "I believe, I believe\nI believe, I believe"
        },
        {
            "start": 4950300,
            "end": 4952970,
            "text": "I believe, I believe\nI believe, I believe, I believe, hey"
        },
        {
            "start": 4953050,
            "end": 4955630,
            "text": "Y\u0027all sing it with me!"
        },
        {
            "start": 4955720,
            "end": 4957390,
            "text": "Believe"
        },
        {
            "start": 4957470,
            "end": 4960430,
            "text": "I believe"
        },
        {
            "start": 4960510,
            "end": 4963430,
            "text": "- I believe\n- I\u0027m a believer"
        },
        {
            "start": 4963520,
            "end": 4973940,
            "text": "I believe"
        },
        {
            "start": 4976360,
            "end": 4978200,
            "text": "Oh, that\u0027s funny.\nOh. Oh."
        },
        {
            "start": 4978600,
            "end": 4982100,
            "text": "I can\u0027t breathe.\nI can\u0027t breathe."
        },
        {
            "start": 4982240,
            "end": 4984540,
            "text": "I believe in self-assertion"
        },
        {
            "start": 4984620,
            "end": 4986790,
            "text": "Destiny or a slight divertion"
        },
        {
            "start": 4986870,
            "end": 4991170,
            "text": "Now it seems I\u0027ve got my head on straight"
        },
        {
            "start": 4991250,
            "end": 4993380,
            "text": "I\u0027m freak an apparition"
        },
        {
            "start": 4993460,
            "end": 4995590,
            "text": "Seems I\u0027ve made the right decision"
        },
        {
            "start": 4995670,
            "end": 4999300,
            "text": "To try to turn back now it might be too late"
        },
        {
            "start": 4999390,
            "end": 5003720,
            "text": "- I want to stay home today\n- Don\u0027t wanna go out"
        },
        {
            "start": 5003810,
            "end": 5008150,
            "text": "- If anyone comes to play\n- Gonna get thrown out"
        },
        {
            "start": 5008230,
            "end": 5012570,
            "text": "- I wanna stay home today\n- Don\u0027t want no company"
        },
        {
            "start": 5012650,
            "end": 5015110,
            "text": "No way"
        },
        {
            "start": 5015190,
            "end": 5017860,
            "text": "Yeah, yeah, yeah"
        },
        {
            "start": 5017950,
            "end": 5020160,
            "text": "I wanna be a millionaire someday"
        },
        {
            "start": 5020240,
            "end": 5022410,
            "text": "But know what it feels like to give it away"
        },
        {
            "start": 5022490,
            "end": 5025910,
            "text": "Watch me march to the beat of my own drum"
        },
        {
            "start": 5026000,
            "end": 5028580,
            "text": "And it\u0027s off to the moon and then bak again"
        },
        {
            "start": 5028670,
            "end": 5030750,
            "text": "Same old day\nSame situation"
        },
        {
            "start": 5030840,
            "end": 5035010,
            "text": "My happiness rears back as if to say"
        },
        {
            "start": 5035090,
            "end": 5039390,
            "text": "- I wanna stay home today\n- Don\u0027t wanna go out"
        },
        {
            "start": 5039470,
            "end": 5043850,
            "text": "- If anyone comes my way\n- Gonna get thrown out"
        },
        {
            "start": 5043930,
            "end": 5048350,
            "text": "- I wanna stay home today\n- Don\u0027t want no company"
        },
        {
            "start": 5048440,
            "end": 5050900,
            "text": "No way"
        },
        {
            "start": 5050980,
            "end": 5053610,
            "text": "Yeah, yeah, yeah"
        },
        {
            "start": 5057450,
            "end": 5062280,
            "text": "I wanna stay home\nstay home, stay home"
        },
        {
            "start": 5062370,
            "end": 5067410,
            "text": "- I wanna stay home today\n- Don\u0027t wanna go out"
        },
        {
            "start": 5067500,
            "end": 5071790,
            "text": "- If anyone comes my way\n- Gonna get thrown out"
        },
        {
            "start": 5071880,
            "end": 5074710,
            "text": "I wanna stay home today"
        },
        {
            "start": 5074800,
            "end": 5078800,
            "text": "Don\u0027t want no company\nNo way"
        },
        {
            "start": 5078880,
            "end": 5081720,
            "text": "Yeah, yeah, yeah"
        },
        {
            "start": 5095520,
            "end": 5099490,
            "text": "I get such a thrill when you look in my eyes"
        },
        {
            "start": 5099570,
            "end": 5103740,
            "text": "My heart skips a beat\nGirl, I feel so alive"
        },
        {
            "start": 5103820,
            "end": 5107580,
            "text": "Please tell me, baby if all this is true"
        },
        {
            "start": 5107660,
            "end": 5111750,
            "text": "\u0027Cause deep down inside allI wanted was you"
        },
        {
            "start": 5111830,
            "end": 5115750,
            "text": "Oh-oh-oh\nMakes me wanna dance"
        },
        {
            "start": 5115840,
            "end": 5119720,
            "text": "Oh-oh-oh\nIt\u0027s a new romance"
        },
        {
            "start": 5119800,
            "end": 5123760,
            "text": "Oh-oh-oh\nI look into your eyes"
        },
        {
            "start": 5123840,
            "end": 5127720,
            "text": "Oh-oh-oh\nThe best years of our lives"
        },
        {
            "start": 5127810,
            "end": 5131600,
            "text": "When we first met I could hardly believe"
        },
        {
            "start": 5131690,
            "end": 5135650,
            "text": "The things that would happen and we could achieve"
        },
        {
            "start": 5135730,
            "end": 5139440,
            "text": "So let\u0027s be together for all of our time"
        },
        {
            "start": 5139530,
            "end": 5143410,
            "text": "Oh, girl, I\u0027m so thankful that you are still mine"
        },
        {
            "start": 5143490,
            "end": 5145570,
            "text": "You always consider me like an ugly duckling"
        },
        {
            "start": 5145660,
            "end": 5148120,
            "text": "And treat me like a Nostradamus was why I had to get my shine on"
        },
        {
            "start": 5148200,
            "end": 5150250,
            "text": "I break a little something to keep my mind on"
        },
        {
            "start": 5150330,
            "end": 5152750,
            "text": "\u0027Cause you had my mond gone Eh-eh, eh-eh, eh-eh"
        },
        {
            "start": 5152830,
            "end": 5155670,
            "text": "Turn the lights on, Come on, baby\nLet\u0027s just rewind the song"
        },
        {
            "start": 5155750,
            "end": 5157880,
            "text": "\u0027Cause all I want to do is make the rest years the best years"
        },
        {
            "start": 5157960,
            "end": 5159960,
            "text": "All night long"
        },
        {
            "start": 5160050,
            "end": 5163760,
            "text": "- Oh-oh-oh, makes me wanna dance\n- Makes me wanna dance"
        },
        {
            "start": 5163840,
            "end": 5167760,
            "text": "- Oh-oh-oh, it\u0027s a new romance\n- It\u0027s a new romance"
        },
        {
            "start": 5167850,
            "end": 5171020,
            "text": "- Oh-oh-oh, I look into your eyes\n- Oh, yeah, yeah"
        },
        {
            "start": 5171100,
            "end": 5173770,
            "text": "- Look into your eyes\n- Oh-oh-oh"
        },
        {
            "start": 5173850,
            "end": 5175980,
            "text": "- The best years of our lives\n- Yeah, yeah, yeah, yeah"
        },
        {
            "start": 5176060,
            "end": 5179780,
            "text": "- Oh-oh-oh, makes me wanna dance\n- Whoa-oh-oh, dance, yeah"
        },
        {
            "start": 5179860,
            "end": 5183780,
            "text": "Oh-oh-oh\nIt\u0027s a new romance"
        },
        {
            "start": 5183860,
            "end": 5187830,
            "text": "- Oh-oh-oh, i look into your eyes\n- Look into your eyes, yeah"
        },
        {
            "start": 5187910,
            "end": 5192540,
            "text": "Oh-oh-oh\nThe best years of our lives"
        },
        {
            "start": 5192620,
            "end": 5195460,
            "text": "Everything looks bright"
        },
        {
            "start": 5195540,
            "end": 5197790,
            "text": "Standing in your light"
        },
        {
            "start": 5197880,
            "end": 5200210,
            "text": "Everything feels right"
        },
        {
            "start": 5200300,
            "end": 5203380,
            "text": "What\u0027s left is out of sight"
        },
        {
            "start": 5203470,
            "end": 5208050,
            "text": "What\u0027s a girl to do I\u0027m\ntelling you You\u0027re on my mind"
        },
        {
            "start": 5208140,
            "end": 5210720,
            "text": "I wanna be with you"
        },
        {
            "start": 5210810,
            "end": 5213100,
            "text": "\u0027Cause when you\u0027re standin\u0027 next to me"
        },
        {
            "start": 5213180,
            "end": 5215100,
            "text": "It\u0027s like wow"
        },
        {
            "start": 5215190,
            "end": 5218310,
            "text": "And all your kisses seem to set me free"
        },
        {
            "start": 5218400,
            "end": 5220230,
            "text": "It\u0027s like wow"
        },
        {
            "start": 5220320,
            "end": 5223650,
            "text": "And when we touch it\u0027s such a rush I can\u0027t get enough"
        },
        {
            "start": 5223740,
            "end": 5226410,
            "text": "It\u0027s like- It\u0027s like\nOoh-ooh"
        },
        {
            "start": 5226490,
            "end": 5229030,
            "text": "Hey, what"
        },
        {
            "start": 5229120,
            "end": 5232410,
            "text": "It\u0027s like wow\nOoh-ooh,hey"
        },
        {
            "start": 5232500,
            "end": 5235750,
            "text": "Hey, yeah\nIt\u0027s like wow"
        },
        {
            "start": 5235830,
            "end": 5239460,
            "text": "Everything is looking right now, right now"
        },
        {
            "start": 5239540,
            "end": 5242760,
            "text": "- It\u0027s like wow\n- And I got this feeling"
        },
        {
            "start": 5242840,
            "end": 5245720,
            "text": "This feeling it\u0027s just like wow"
        },
        {
            "start": 5245800,
            "end": 5248050,
            "text": "It\u0027s just like wow"
        },
        {
            "start": 5248140,
            "end": 5250100,
            "text": "- You are all I\u0027m thinking of."
        },
        {
            "start": 5250180,
            "end": 5254560,
            "text": "Everything feels right\nEverything feels right"
        },
        {
            "start": 5254640,
            "end": 5257060,
            "text": "- Like wow\n- Everything looks bright"
        },
        {
            "start": 5257140,
            "end": 5259730,
            "text": "All my senses are right"
        },
        {
            "start": 5259810,
            "end": 5262190,
            "text": "- Like wow\n- Everything feels right"
        },
        {
            "start": 5262270,
            "end": 5265610,
            "text": "Baby, baby, baby the way I\u0027m feeling you"
        },
        {
            "start": 5265690,
            "end": 5267610,
            "text": "Is like wow"
        },
        {
            "start": 5288220,
            "end": 5291760,
            "text": "There is something that I see"
        },
        {
            "start": 5291850,
            "end": 5295270,
            "text": "In the way you look at me"
        },
        {
            "start": 5295350,
            "end": 5298770,
            "text": "There\u0027s a smile\nThere\u0027s a truth"
        },
        {
            "start": 5298850,
            "end": 5302270,
            "text": "In your eyes"
        },
        {
            "start": 5302360,
            "end": 5305690,
            "text": "What an unexpected way"
        },
        {
            "start": 5305780,
            "end": 5309160,
            "text": "On this unexpected day"
        },
        {
            "start": 5309240,
            "end": 5311160,
            "text": "Could it be"
        },
        {
            "start": 5311240,
            "end": 5316160,
            "text": "This is where I belong"
        },
        {
            "start": 5316250,
            "end": 5319620,
            "text": "It is you I have loved"
        },
        {
            "start": 5319710,
            "end": 5322710,
            "text": "All along"
        },
        {
            "start": 5322790,
            "end": 5326510,
            "text": "There\u0027s no more mystery"
        },
        {
            "start": 5326590,
            "end": 5330050,
            "text": "It\u0027s finally clear to me"
        },
        {
            "start": 5330130,
            "end": 5333800,
            "text": "You\u0027re the home my heart\u0027s searched for"
        },
        {
            "start": 5333890,
            "end": 5336890,
            "text": "So long"
        },
        {
            "start": 5336970,
            "end": 5340560,
            "text": "It is you I have loved"
        },
        {
            "start": 5340640,
            "end": 5344570,
            "text": "All along"
        },
        {
            "start": 5371760,
            "end": 5375600,
            "text": "Whoa, over and over"
        },
        {
            "start": 5375680,
            "end": 5380770,
            "text": "I\u0027m filled with emotion"
        },
        {
            "start": 5380850,
            "end": 5383810,
            "text": "As I look"
        },
        {
            "start": 5383900,
            "end": 5388530,
            "text": "Into your perfect face"
        }
    ],
    "phrases": [
        {
            "phrase": {
                "lineId": 0,
                "phrase": "Once upon a time",
                "translation": {
                    "main": "Давным-давно"
                }
            },
            "handled": true
        },
        {
            "phrase": {
                "lineId": 1,
                "phrase": "sort",
                "translation": {
                    "main": "Сортировать",
                    "groups": [
                        {
                            "partOfSpeech": "глагол",
                            "variants": [
                                "сортировать",
                                "классифицировать",
                                "разбирать"
                            ]
                        },
                        {
                            "partOfSpeech": "имя существительное",
                            "variants": [
                                "сорт",
                                "род",
                                "разновидность",
                                "образ",
                                "разряд",
                                "манера",
                                "литеры",
                                "характер",
                                "способ",
                                "качество"
                            ]
                        }
                    ]
                }
            }
        },
        {
            "phrase": {
                "lineId": 3,
                "phrase": "castle",
                "translation": {
                    "main": "замок",
                    "groups": [
                        {
                            "partOfSpeech": "имя существительное",
                            "variants": [
                                "замок",
                                "дворец",
                                "ладья",
                                "твердыня",
                                "рокировка",
                                "убежище"
                            ]
                        },
                        {
                            "partOfSpeech": "глагол",
                            "variants": [
                                "рокировать",
                                "рокироваться"
                            ]
                        }
                    ]
                }
            }
        }
    ]
}
