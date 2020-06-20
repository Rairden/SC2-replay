package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static main.Matchup.*;

public class Main extends TimerTask {
    
    FileManager fileMgr;
    static int[] scoreZvP = new int[2];
    static int[] scoreZvT = new int[2];
    static int[] scoreZvZ = new int[2];
    static final String PYTHON_PATH = "C:\\Python\\Python382\\python.exe";
    static final String SCRIPT_PATH = "src/main/resources/printReplay.py";
    static final String REPLAY_PATH = "src/main/resources/replays";

    public Main() {
        this.fileMgr = new FileManager();
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        Main timerTask = new Main();
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void run() {
        // if (fileMgr.numberOfFiles() == fileMgr.numFiles) return;

        scoreZvP[0] = scoreZvP[1] = 0;
        scoreZvT[0] = scoreZvT[1] = 0;
        scoreZvZ[0] = scoreZvZ[1] = 0;

        try {
            replayReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replayReader() throws IOException {
        Scanner scan;
        String linuxCMD = String.format("%s %s %s", PYTHON_PATH, SCRIPT_PATH, REPLAY_PATH);
        String win10CMD = String.format("cmd /c %s %s %s", PYTHON_PATH, SCRIPT_PATH, REPLAY_PATH);
        Process p = Runtime.getRuntime().exec(win10CMD);
        InputStream is = p.getInputStream();

        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            sb.append((char) i);
        }

        scan = new Scanner(sb.toString());
        scan.nextLine();
        scan.nextLine();

        while (scan.hasNextLine()) {
            parseReplay(scan);
        }

        System.out.println("ZvP = " + scoreZvP[0] + " - " + scoreZvP[1]);
        System.out.println("ZvT = " + scoreZvT[0] + " - " + scoreZvT[1]);
        System.out.println("ZvZ = " + scoreZvZ[0] + " - " + scoreZvZ[1]);

        // save files to current jar execution dir
        saveFile(System.getProperty("user.dir") + File.separator, "ZvP.txt", scoreZvP);
        saveFile(System.getProperty("user.dir") + File.separator, "ZvT.txt", scoreZvT);
        saveFile(System.getProperty("user.dir") + File.separator, "ZvZ.txt", scoreZvZ);
    }

    private void saveFile(String dir, String fileName, int[] score) throws IOException {
        fileMgr.save(dir + fileName, score);
    }

    private static void parseReplay(Scanner scan) {
        Matchup m = null;
        Player p1 = null;
        Player p2 = null;

        List<Player> players = new ArrayList<>();

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.matches("^-+")) break;

            if (line.matches("^Win: Player [12].*")) {
                String[] s = line.split("\\s");
                p1 = new Player(s[4], s[5], 1);
                players.add(p1);
            }
            if (line.matches("^Loss: Player [12].*")) {
                String[] s = line.split("\\s");
                p2 = new Player(s[4], s[5], 0);
                players.add(p2);
            }
        }

        outer:
        for (int i = 0; i < 2; i++) {
            switch (players.get(i).race) {
                case "(Zerg)" -> m = ZvZ;
                case "(Protoss)" -> {
                    m = ZvP;
                    break outer;
                }
                case "(Terran)" -> {
                    m = ZvT;
                    break outer;
                }
            }
        }

        Replay rep = new Replay(p1, p2, m);
        setScore(rep, m);
    }

    private static void setScore(Replay rep, Matchup m) {
        if (rep.p1.name.matches("Gixxasaurus|Rairden")) {
            assignScore(rep, m, 0, 1);
        } else {
            assignScore(rep, m, 1, 0);
        }
    }

    private static void assignScore(Replay rep, Matchup m, int i, int k) {
        switch (m) {
            case ZvP -> {
                if (rep.p1.win == 1)
                    scoreZvP[i]++;
                else
                    scoreZvP[k]++;
            }
            case ZvT -> {
                if (rep.p1.win == 1)
                    scoreZvT[i]++;
                else
                    scoreZvT[k]++;
            }
            case ZvZ -> {
                if (rep.p1.win == 1)
                    scoreZvZ[i]++;
                else
                    scoreZvZ[k]++;
            }
        }
    }
}
