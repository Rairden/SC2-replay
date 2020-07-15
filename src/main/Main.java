package main;

import java.io.*;
import java.util.*;

import static main.FileManager.*;
import static main.Settings.*;

public class Main extends TimerTask {

    FileManager fileMgr;
    static int startMMR;
    static int currentMMR;
    static int[] scoreZvP = new int[2];
    static int[] scoreZvT = new int[2];
    static int[] scoreZvZ = new int[2];
    static String[] cmdAllReplays;
    static String[] cmdSingleReplay;
    static boolean firstLoop = true;

    public Main() {
        fileMgr = new FileManager();
        cmdAllReplays = new String[]{PATH_PYTHON, PATH_SCRIPT, DIR_REPLAYS};
        cmdSingleReplay = new String[]{PATH_PYTHON, PATH_SCRIPT, ""};
    }

    public static void main(String[] args) throws IOException {
        Settings settings = new Settings();
        settings.loadMMR();
        Main timerTask = new Main();

        if (numberOfFiles() > 0) {
            File oldestFile = getLastModified(false);

            cmdSingleReplay[2] = oldestFile.toString();
            StringBuilder python_stdout = cmd(cmdSingleReplay);
            Scanner scan = new Scanner(python_stdout.toString());

            while (scan.hasNextLine()) {
                String[] nameMMR = scan.nextLine().split("\\s");
                String name = nameMMR[0];
                if (!name.matches(PLAYER)) continue;

                int mmr = Integer.parseInt(nameMMR[1]);
                if (mmr <= 0) break;
                startMMR = mmr;
            }
        }

        Timer timer = new Timer();
        timerTask.saveAllFiles();
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void run() {
        try {
            if (!firstLoop) {
                if (fileMgr.numFiles == numberOfFiles()) return;
            }
            firstLoop = false;
            fileMgr.numFiles = numberOfFiles();

            Arrays.fill(scoreZvP, 0);
            Arrays.fill(scoreZvT, 0);
            Arrays.fill(scoreZvZ, 0);

            replayReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static StringBuilder cmd(String[] cmd) throws IOException {
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is = p.getInputStream();
        return getPythonOutput(is);
    }

    void replayReader() throws IOException {
        File newestReplay = getLastModified(true);
        if (newestReplay != null) {
            cmdSingleReplay[2] = newestReplay.toString();
        }

        StringBuilder python_stdout = cmd(cmdAllReplays);
        Scanner scan = new Scanner(python_stdout.toString());

        while (scan.hasNextLine()) {
            parsePythonOutput(scan);
        }

        saveAllFiles();

        StringBuilder python_stdout1 = cmd(cmdSingleReplay);
        Scanner scan1 = new Scanner(python_stdout1.toString());

        while (scan1.hasNextLine()) {
            String[] nameMMR = scan1.nextLine().split("\\s");
            String name = nameMMR[0];

            if (name.matches(PLAYER)) {
                int mmr = Integer.parseInt(nameMMR[1]);
                if (mmr <= 0) break;
                currentMMR = mmr;

                if (numberOfFiles() == 1 && fileMgr.readMMR() == 0) {
                    startMMR = mmr;
                }
                break;
            }
        }

        fileMgr.saveMMR(currentMMR);
    }

    // save files to the current jar execution directory
    void saveAllFiles() throws IOException {
        saveFile(fileMgr.ZvP_txt, scoreZvP);
        saveFile(fileMgr.ZvT_txt, scoreZvT);
        saveFile(fileMgr.ZvZ_txt, scoreZvZ);
    }

    static StringBuilder getPythonOutput(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            sb.append((char) i);
        }
        return sb;
    }

    static void parsePythonOutput(Scanner scan) {
        String line = scan.nextLine();
        if (!line.matches("Zv[PTZ].*")) return;

        String[] s = line.split("\\s");
        String matchup = s[0];
        String name = s[1];

        switch (matchup) {
            case "ZvP" -> setScore(name, scoreZvP);
            case "ZvT" -> setScore(name, scoreZvT);
            case "ZvZ" -> setScore(name, scoreZvZ);
        }
    }

    static int setScore(String name, int[] scoreZvX) {
        return name.matches(PLAYER) ? scoreZvX[0]++ : scoreZvX[1]++;
    }
}
