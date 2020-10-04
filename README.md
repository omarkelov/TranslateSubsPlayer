# TranslateSubsPlayer

**TranslateSubsPlayer** is an *educational* project, aimed to create a software for learning foreign languages through watching videos with original voice acting and original subtitles.

Upon completion of the project it is supposed to get two applications:
* Desktop cross-platform video player with interactive translatable subtitles;
* Client-Server application, used in bundle with video player for reviewing studied material.

# Project description

A user can open a video file of one of the popular formats in the player, select the desired audio track and subtitles; while viewing, he can click any word/phrase in the subtitles and immediately get the translation in a pop-up window. All translated words are remembered by the player, and after watching the user can save them to the dictionary along with the context. After that, the user can view this dictionary, as well as start a test on the material studied. 
The dictionary is stored on a remote server (for the further possibility of adding the player to other platforms while maintaining access to a single database). Viewing the dictionary and testing is available in the web application.

# Possible risks

* This is our first experience of working with video (though, it seems several popular libraries for working with video are present, as well as their wrappers in different programming languages).
* ...

# Possible technologies:

###### Desktop video player:
* Qt + libVLC (+ ffmpeg?) – seems too *time-consuming*;
* JavaFX + vlcj (+ ffmpeg?) – seems too *resource-consuming*.

###### Server application:
* Java's embedded HttpServer – seems too *outdated*;
* Java Spring framework – seems too *heavy*.

###### Client application:
* Web-application.

# MVP release dates

* **21.12.2020** – *Player* with basic functional.
* **21.12.2020** – *Client-Server app* with basic functional.
* **01.03.2021** – *Player* and *Client-Server app* bundled together.

# Release dates

* **29.03.2021** – release candidate.
* **24.05.2021** – production release.

# Team members

* Oleg Markelov
* Polina Karaseva
* ~~Ivan Shatalov~~ – took a study leave
