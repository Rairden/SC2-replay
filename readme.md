I wrote a web [scraper](https://gitlab.com/Rairden/SC2-StatsScraper/-/tree/release-win10) for Starcraft 2.

Unlike the web scraper, this program doesn't require using [sc2replaystats.com](https://sc2replaystats.com/). It simply uses a python module [sc2reader](https://pypi.org/project/sc2reader/) to parse your local replay files. Since I don't know python, I used Java to parse the python stdout and keep track of your win/loss stats. Java checks if your replay folder has a new file (every 5 seconds), and if so, writes your stats to five text files in the current folder.

![overlay](img/SC2-overlay.png)

## How to install

1. Install Java 14 - Java(TM) SE Runtime Environment (build 14.0.1+7)
2. Build and create a jar from source code.
3. Place the application (SC2-replay.jar) anywhere on your system.
4. Install python and add it to your PATH environment variable (optional).
5. Install the python `sc2reader`* module.

```sh
pip install sc2reader
```

And finally run the program:

```sh
java -jar SC2-replay.jar
```

When you first run it, the jar will create two new files:

* settings.cfg
* printReplayShort.py

In order for the program to run, you must modify two lines in `settings.cfg`.

```sh
replays=E:\SC2\replayBackup
player=Gixxasaurus|Rairden
```

Change this line to wherever SC2 saves replays, or scelight is configured to backup replays. And change the player line to your SC2 account name(s), separated by a vertical bar.

After you play SC2 or put a replay in your watch folder, the app will generate 5 .txt files you can use as overlays in something like OBS for streaming:

* ZvP.txt
* ZvT.txt
* ZvZ.txt
* MMR.txt
* MMR-diff.txt

## How the program works

1. loads your personal settings from `settings.cfg`
2. the app doesn't do anything unless a new file is detected every 5 seconds in the replay folder
3. call `printReplayShort.py` from java, then parses the output with java
    - 1st cmd = calc winrates of entire folder (3 - 7, etc)
    - 2nd cmd = calc the newest replays' MMR rating

```sh
python printReplayShort.py E:\SC2\replayBackup
python printReplayShort.py newestReplay.SC2Replay
```

4. saves output to txt files of your current MMR, and the difference of your start/end MMR
5. does not save to disk if the stats have not changed (for Zv*.txt)

The program works for many different combinations. The replay folder can start empty, or have replays in it. Your `MMR.txt` can be default `0` or `3100` to start.

So how do I use this app? Have 2 replays folders. One archive folder with 100's of replays, and one empty folder. Before you play, move your 10 session replays from your watch folder to your archive folder so it's empty. Now run the app.

I really like this program compared to my web scraper. The scraper would take 70 seconds (waiting on sc2replaystats.com to process the replay). The updated [v1](https://gitlab.com/Rairden/SC2-replay/-/blob/9887e88367a81ea22480b6377c49af42bc322432/src/main/Main.java) version has no delay (it takes 150 ms on 8 replays). On this v2 with a MMR counter, it takes 450 ms (1 replay or 8 replays).

| app         | lines of code | runtime | MMR counter |
|-------------|:-------------:|:-------:|:-----------:|
| Web scraper |      551      |         |          no |
| This app v1 |      251      |  150 ms |          no |
| This app v2 |      401      |  450 ms |         yes |

\* My printReplayShort.py script also needs the package called `mpyq`. As of this writing, when you install `sc2reader` it auto-installs `mpyq`.

