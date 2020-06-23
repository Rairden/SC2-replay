package main;

import java.io.*;
import java.util.*;

import static main.Settings.*;

public class Main extends TimerTask {
    
    FileManager fileMgr;
    static int[] scoreZvP = new int[2];
    static int[] scoreZvT = new int[2];
    static int[] scoreZvZ = new int[2];
    String cmd = String.format("%s %s %s", PATH_PYTHON, PATH_SCRIPT, DIR_REPLAYS);

    public Main() {
        this.fileMgr = new FileManager();
    }

    public static void main(String[] args) throws IOException {
        new Settings();
        Timer timer = new Timer();
        Main timerTask = new Main();
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void run() {
        if (fileMgr.numFiles == fileMgr.numberOfFiles()) return;
        fileMgr.numFiles = fileMgr.numberOfFiles();

        Arrays.fill(scoreZvP, 0);
        Arrays.fill(scoreZvT, 0);
        Arrays.fill(scoreZvZ, 0);

        try {
            replayReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replayReader() throws IOException {
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is = p.getInputStream();

        StringBuilder pythonStdOut = getPythonOutput(is);
        Scanner scan = new Scanner(pythonStdOut.toString());

        while (scan.hasNextLine()) {
            parsePythonOutput(scan);
        }

        // save files to the current jar execution dir
        saveFile(System.getProperty("user.dir") + File.separator, "ZvP.txt", scoreZvP);
        saveFile(System.getProperty("user.dir") + File.separator, "ZvT.txt", scoreZvT);
        saveFile(System.getProperty("user.dir") + File.separator, "ZvZ.txt", scoreZvZ);
    }

    private void saveFile(String dir, String fileName, int[] score) throws IOException {
        fileMgr.save(dir + fileName, score);
    }

    private StringBuilder getPythonOutput(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            sb.append((char) i);
        }
        return sb;
    }

    private static void parsePythonOutput(Scanner scan) {
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (!line.matches("Zv[PTZ].*")) continue;

            String[] s = line.split("\\s");
            String matchup = s[0];
            String name = s[1];

            switch (matchup) {
                case "ZvP" -> setScore(name, scoreZvP);
                case "ZvT" -> setScore(name, scoreZvT);
                case "ZvZ" -> setScore(name, scoreZvZ);
            }
        }
    }

    private static int setScore(String name, int[] scoreZvX) {
        return name.matches(PLAYER) ? scoreZvX[0]++ : scoreZvX[1]++;
    }
}
