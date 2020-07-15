package main;

import java.io.*;
import java.util.*;

import static main.Settings.*;

public class Main extends TimerTask {
    
    FileManager fileMgr;
    static int[] scoreZvP = new int[2];
    static int[] scoreZvT = new int[2];
    static int[] scoreZvZ = new int[2];
    static int startMMR = 0;
    static int[] currentMMR = new int[1];
    static boolean reset = false;
    String[] cmd1 = {PATH_PYTHON, PATH_SCRIPT, ""};
    String[] cmd2 = {PATH_PYTHON, PATH_SCRIPT, DIR_REPLAYS};

    public Main() {
        this.fileMgr = new FileManager();
    }

    public static void main(String[] args) throws IOException {
        Settings settings = new Settings();
        startFromNonEmptyDir(args);
        settings.loadMMR();
        Timer timer = new Timer();
        Main timerTask = new Main();
        timer.schedule(timerTask, 0, 5000);
    }

    private static void startFromNonEmptyDir(String[] args) {
        if (args.length == 0) return;
        if (args[0].equals("--notempty")) reset = true;
    }

    @Override
    public void run() {
        try {
            if (reset) {
                replayReader(cmd2);
            } else {
                if (fileMgr.numberOfFiles() == fileMgr.numFiles) return;
                File newestReplay = fileMgr.getLastModified();
                if (newestReplay == null) return;
                cmd1[2] = newestReplay.toString();
                replayReader(cmd1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replayReader(String[] cmd) throws IOException {
        fileMgr.numFiles = fileMgr.numberOfFiles();
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is = p.getInputStream();

        StringBuilder pythonStdOut = getPythonOutput(is);
        Scanner scan = new Scanner(pythonStdOut.toString());

        String matchup = "";
        while (scan.hasNextLine()) {
            matchup = parsePythonOutput(scan);
        }

        // save files to the current jar execution dir
        if (reset) {
            saveFile(System.getProperty("user.dir") + File.separator, "ZvP.txt", scoreZvP);
            saveFile(System.getProperty("user.dir") + File.separator, "ZvT.txt", scoreZvT);
            saveFile(System.getProperty("user.dir") + File.separator, "ZvZ.txt", scoreZvZ);
            reset = false;
        } else {
            switch (matchup) {
                case "ZvP" -> saveFile(System.getProperty("user.dir") + File.separator, "ZvP.txt", scoreZvP);
                case "ZvT" -> saveFile(System.getProperty("user.dir") + File.separator, "ZvT.txt", scoreZvT);
                case "ZvZ" -> saveFile(System.getProperty("user.dir") + File.separator, "ZvZ.txt", scoreZvZ);
            }
            saveMMR(System.getProperty("user.dir") + File.separator);
        }
    }

    private void saveFile(String dir, String fileName, int[] score) throws IOException {
        fileMgr.save(dir + fileName, score);
    }

    private void saveMMR(String dir) throws IOException {
        fileMgr.save(dir + "MMRdiff.txt", currentMMR);
    }

    private StringBuilder getPythonOutput(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            sb.append((char) i);
        }
        return sb;
    }

    private static String parsePythonOutput(Scanner scan) {
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (!line.matches("Zv[PTZ].*")) continue;

            String[] s = line.split("\\s");
            String matchup = s[0];
            String player = s[1];

            while (scan.hasNextLine()) {
                String mmr = scan.nextLine();
                String[] nameMMR = mmr.split("\\s");

                if (nameMMR[0].matches(PLAYER)) {
                    if (Integer.parseInt(nameMMR[1]) <= 0) break;
                    currentMMR[0] = Integer.parseInt(nameMMR[1]);
                }
            }

            if (matchup.equals("ZvP")) {
                setScore(player, scoreZvP);
                return "ZvP";
            }
            if (matchup.equals("ZvT")) {
                setScore(player, scoreZvT);
                return "ZvT";
            }
            if (matchup.equals("ZvZ")) {
                setScore(player, scoreZvZ);
                return "ZvZ";
            }
        }
        return null;
    }

    private static int setScore(String name, int[] scoreZvX) {
        return name.matches(PLAYER) ? scoreZvX[0]++ : scoreZvX[1]++;
    }
}
