package main;

import java.io.*;
import java.util.*;

public class Main extends TimerTask {
    
    FileManager fileMgr;
    static int[] scoreZvP = new int[2];
    static int[] scoreZvT = new int[2];
    static int[] scoreZvZ = new int[2];
    static final String PYTHON_PATH = "C:\\Python\\Python382\\python.exe";
    static final String SCRIPT_PATH = "src/main/resources/printReplayShort.py";
    static final String REPLAY_PATH = "E:\\SC2\\replayBackup";
    static final String toon = "Gixxasaurus|Rairden";

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
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (!line.matches("Zv[PTZ].*")) continue;

            String[] s = line.split("\\s");

            switch (s[0]) {
                case "ZvP" -> setScore(s, scoreZvP);
                case "ZvT" -> setScore(s, scoreZvT);
                case "ZvZ" -> setScore(s, scoreZvZ);
            }
        }
    }

    private static int setScore(String[] s, int[] scoreZvX) {
        return s[1].matches(toon) ? scoreZvX[0]++ : scoreZvX[1]++;
    }
}
