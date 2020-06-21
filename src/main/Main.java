package main;

import java.io.*;
import java.util.*;

import static main.Settings.*;

public class Main extends TimerTask {
    
    FileManager fileMgr;
    static int[] scoreZvP = new int[2];
    static int[] scoreZvT = new int[2];
    static int[] scoreZvZ = new int[2];

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
        String linuxCMD = String.format("%s %s %s", PATH_PYTHON, PATH_SCRIPT, DIR_REPLAYS);
        String win10CMD = String.format("cmd /c %s %s %s", PATH_PYTHON, PATH_SCRIPT, DIR_REPLAYS);
        Process p = Runtime.getRuntime().exec(linuxCMD);
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

        System.out.println("ZvP: " + scoreZvP[0] + " - " + scoreZvP[1]);
        System.out.println("ZvT: " + scoreZvT[0] + " - " + scoreZvT[1]);
        System.out.println("ZvZ: " + scoreZvZ[0] + " - " + scoreZvZ[1]);

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
        return s[1].matches(PLAYER) ? scoreZvX[0]++ : scoreZvX[1]++;
    }
}
