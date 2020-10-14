# Moduls

*TranslateSubsPlayer* project consists of three separate modules: **Desktop Player**, **Web-Client** and **Http Server**.

![Moduls](https://funkyimg.com/i/37Yr3.png)

### Desktop Player

**Main functions:**
* play a local video;
* translate some separate parts of subtitles;
* fill in the dictionary with translated phrases;
* manage the dictionary;
* take a test.

**Responsible members:** @omarkelov.

**List of technologies:**
* [JavaFX](https://openjfx.io/) for UI;
* [vlcj](https://capricasoftware.co.uk/projects/vlcj) for video playback;
* (?) [FFmpeg](https://ffmpeg.org/) for video encoding;
* [SQLite](https://www.sqlite.org) for database handler.

### Web-Client

**Main functions:**
* manage the dictionary;
* take a test.

**Responsible members:** @pkaraseva18214.

**List of technologies:**
* [Bootstrap](https://getbootstrap.com/) for UI;
* (?) [jQuery](https://jquery.com/) for cross-browsing.

### Http Server

**Main functions:**
* respond to Desktop Player and Web-client requests;
* synchonize data with Desktop Player.

**Responsible members:** @omarkelov, @pkaraseva18214.

**List of technologies:**
* [Spring](https://spring.io/) for backend engine;
* (?) [SQLite](https://www.sqlite.org) for database handler.
