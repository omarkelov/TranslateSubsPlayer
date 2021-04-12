### RESTful API:
* **POST** /registration?username=`<username>`&password=`<password>`
  * registers user
  * *status code* **204 No Content**
* **POST** /login?username=`<username>`&password=`<password>`
  * logs user in
  * *status code* **204 No Content**
* **POST** /logout
  * logs user out
  * *status code* **204 No Content**

---

* **GET** /raw-movies
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_raw_movies_example.json))
  * *returns* a list of raw movies
* **GET** /raw-movies/`<rawMovieId>`
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_raw_movie_example.json))
  * *returns* a list of lines (subtitles) and raw phrases for this raw movie
* **DELETE** /raw-movies/`<rawMovieId>`
  * *status code* **204 No Content**
  * *deletes* this raw movie from the database
* **POST** /raw-movies
  * body: {hashSum=`<hashSum>`&videoFilePath=`<videoFilePath>`&linesJson=`<linesJson>`}
  * *status code* **204 No Content**
  * creates raw movie
* **HEAD** /raw-movie?hashSum=`<hashSum>`
  * *status code* **204 No Content**
  * checks raw movie existence by its hash sum
* **POST** /raw-phrase?hashSum=`<hashSum>`
  * body: {phraseJson=`<phraseJson>`}
  * *status code* **204 No Content**
  * creates raw phrase and binds it with raw movie by its hash sum

---

* **GET** /movies
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_movies_example.json))
  * *returns* a list of movies
* **GET** /movies/`<movieName>`
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_movie_example.json))
  * *returns* a list of contexts for this movie
* **DELETE** /movies/`<movieName>`
  * *status code* **204 No Content**
  * *deletes* this movie from the database

---

* **GET** /context?phraseId=`<phraseId>`
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_context_example.json))
  * *returns* a context associated with this phrase
* **GET** /contexts/`<contextId>`
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_context_example.json))
  * *returns* this context
* **DELETE** /contexts/`<contextId>`
  * *status code* **204 No Content**
  * *deletes* this context

---

* **GET** /movies/`<movieName>`/test
  * *status code* **200 OK** ([Response example](https://github.com/omarkelov/TranslateSubsPlayer/blob/master/Documentation/Response%20examples/get_test_example.json))
  * *returns* a list of phrase ids
* **PATCH** /phrases/`<phraseId>`?correct=<true/false>`
  * *status code* **204 No Content**
  * updates the database with this answer
