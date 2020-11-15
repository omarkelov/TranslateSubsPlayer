### Pages:
* **GET** /login
  * *returns* login page
* **GET** /movies
  * *returns* page with a list of the movies
* **GET** /`<movieName>`
  * *returns* page with a list of the contexts for this movie
* **GET** /`<movieName>`/test
  * *returns* testing page for this movie

### RESTful API:
* **POST** /log.in?{"username":`<username>`,"password":`<password>`}
  * logs user in
  * *redirects* to the /movies page
* **POST** /log.out
  * logs user out
  * *redirects* to the /login page

* **DELETE** /`<movieName>`
  * deletes this movie from the database

* **DELETE** /context.delete?{"movieName":`<movieName>`,"contextId":`<contextId>`}
  * deletes this context from the database

* **POST** /test?{"movieName":`<movieName>`,"contextId":`<contextId>`,"phraseId":`<phraseId>`,"correct":`<true/false>`}
  * updates the database with this answer
