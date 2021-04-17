const dictionary = {
    "name": "Shrek",
    "contexts": [
        {
            "id": 3,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort...",
            "link": "videos/0.mp4",
            "phrases": [
                {
                    "id": 4,
                    "phrase": "Once upon a time",
                    "translation": "Давным-давно",
                    "phraseStats": {
                        "successfulAttempts": 0,
                        "attempts": 0
                    }
                },
                {
                    "id": 6,
                    "phrase": "sort",
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "phraseStats": {
                        "successfulAttempts": 0,
                        "attempts": 0
                    }
                }
            ]
        },
        {
            "id": 8,
            "context": "She was locked away in a castle... guarded by a terrible fire-breathing dragon.",
            "link": "videos/1.mp4",
            "phrases": [
                {
                    "id": 9,
                    "phrase": "castle",
                    "correctedPhrase": "a castle",
                    "type": "имя существительное",
                    "translation": "замок | дворец",
                    "phraseStats": {
                        "successfulAttempts": 0,
                        "attempts": 0
                    }
                }
            ]
        },
        {
            "id": 11,
            "context": "Once upon a time there was a lovely princess. But she had an enchantment upon her of a fearful sort... which could only be broken by love's first kiss. She was locked away in a castle... guarded by a terrible fire-breathing dragon. Many brave knights had attempted to free her from this deadful prison. But none prevailed. She waited in the dragon's keep... in the highest room of the tallest tower... for her true love and true love's first kiss. Like that's ever gonna happen.",
            "link": "videos/2.mp4",
            "phrases": [
                {
                    "id": 12,
                    "phrase": "Once upon a time",
                    "translation": "Давным-давно",
                    "phraseStats": {
                        "successfulAttempts": 0,
                        "attempts": 0
                    }
                },
                {
                    "id": 14,
                    "phrase": "sort",
                    "type": "имя существительное",
                    "translation": "сорт | род",
                    "phraseStats": {
                        "successfulAttempts": 0,
                        "attempts": 0
                    }
                },
                {
                    "id": 16,
                    "phrase": "castle",
                    "correctedPhrase": "a castle",
                    "type": "имя существительное",
                    "translation": "замок | дворец",
                    "phraseStats": {
                        "successfulAttempts": 0,
                        "attempts": 0
                    }
                }
            ]
        }
    ]
};

var conts = [];

for (let i = 0; i < dictionary.contexts.length; i++) {
    conts.push({
        id: dictionary.contexts[i].id,
        title: dictionary.contexts[i].context
    })
}

const app = Vue.createApp({
    data() {
        return {
            newTodoText: '',
            todos: conts,
            nextTodoId: 1
        }
    },
    methods: {
        addNewTodo() {
            this.todos.push({
                id: this.nextTodoId++,
                title: this.newTodoText
            })
            this.newTodoText = ''
        }
    }
})

app.component('todo-item', {
    template: `
          <li>
            {{ title }}
            <div class="button watch-button" onclick="toggle()"></div>
            <div class="trailer">
                <video src="sea.mp4" controls="controls"></video>
                <img src="close.png" class="close" onclick="toggle()" alt="close">
            </div>
            <div class="button edit-button" onclick="showMsg()"></div>
            <div class="button delete-button" onclick="remove(this)"></div>
          </li>
        `,
    props: ['title'],
    emits: ['remove']
})


app.component('movie-name', {
    data() {
        return {
            movieName: dictionary.name
        }
    },
    template: `
    <div id="header" class="header">{{ movieName }}</div>`
})

app.component('take-test-button', {
    data() {
        return {
            deleteButton: 'Take a test'
        }
    },
    template: `
    <button class="test-button">{{ deleteButton }}</button>`
})

app.mount('#app')




function toggle() {
    let trailer = document.querySelector(".trailer")
    let video = document.querySelector("video")
    trailer.classList.toggle("active");
    video.pause();
    video.currentTime = 0;
}


function showMsg() {
    alert("Edit");
}


// Добавление тегов (для всплывающего перевода)
let all = document.querySelectorAll("li");
let length = all.length;
let l = 0;
for (let i = 0; i < length; i++) {
    l = dictionary.contexts[i].phrases.length;
    for (let j = 0; j < l; j++) {
        all[i].innerHTML = all[i].innerHTML.replace(dictionary.contexts[i].phrases[j].phrase, `<span data-tooltip=${dictionary.contexts[i].phrases[j].translation}>${dictionary.contexts[i].phrases[j].phrase}</span>`);
    }
}


// Добавление всплывающего перевода
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

