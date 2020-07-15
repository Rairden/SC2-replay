package main;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import static main.Main.*;
import static main.Settings.DIR_REPLAYS;

public class FileManager {

    int numFiles;
    static File replayDir;
    File MMRdiff_txt;
    File MMR_txt;
    File ZvP_txt;
    File ZvT_txt;
    File ZvZ_txt;

    public FileManager() {
        replayDir = new File(DIR_REPLAYS);
        numFiles = numberOfFiles();
        MMRdiff_txt =  new File(System.getProperty("user.dir") + File.separator + "MMR-diff.txt");
        MMR_txt =  new File(System.getProperty("user.dir") + File.separator + "MMR.txt");
        ZvP_txt =  new File(System.getProperty("user.dir") + File.separator + "ZvP.txt");
        ZvT_txt =  new File(System.getProperty("user.dir") + File.separator + "ZvT.txt");
        ZvZ_txt =  new File(System.getProperty("user.dir") + File.separator + "ZvZ.txt");
    }

    static void saveFile(File f, int[] score) throws IOException {
        if (!isModified(f, score)) return;
        writeFile(f, score);
    }

    static void writeFile(File f, int[] score) throws IOException {
        StringBuilder sb = new StringBuilder();
        String race1 = String.format("%2s", score[0]);

        sb.append(race1).append(" - ").append(score[1]);

        FileWriter fw = new FileWriter(f);
        fw.write(sb.toString());
        fw.close();
    }

    void saveMMR(int mmr) throws IOException {
        writeMMRDiff(MMRdiff_txt, mmr);
        writeMMR(mmr);
    }

    int readMMR() throws IOException {
        Scanner scan = new Scanner(MMR_txt);
        return Integer.parseInt(scan.nextLine());
    }

    void writeMMR(int mmr) throws IOException {
        FileWriter fw = new FileWriter(MMR_txt);

        fw.write(mmr + "\n");
        fw.close();
    }

    void writeMMRDiff(File f, int mmr) throws IOException {
        FileWriter fw = new FileWriter(f);

        if (numFiles == 0) {
            fw.write("+0 MMR\n");
            fw.close();
            return;
        }

        int difference = startMMR - mmr;
        String result = "";

        if (difference <= 0) {
            difference *= -1;
            result = String.format("+%s MMR", difference);
        } else {
            result = String.format("-%s MMR", difference);
        }

        fw.write(result + "\n");
        fw.close();
    }

    /**
     * There's no need to write to disk if the data hasn't changed.
     *
     * @return false if the existing file matches the data that has been parsed.
     * @throws IOException If an I/O error occurs
     */
    static boolean isModified(File file, int[] score) throws IOException {
        if (file.length() > 0) {
            BufferedReader br = new BufferedReader(new FileReader(file.toString()));
            String str;

            while ((str = br.readLine()) != null) {
                String[] strArr = str.trim().split("\\s");
                int[] arr = {Integer.parseInt(strArr[0]), Integer.parseInt(strArr[2])};

                if (Arrays.hashCode(arr) == Arrays.hashCode(score)) return false;
            }
        }
        return true;
    }

    /**
     * @param lastModified True if you want the most recent file, otherwise the oldest file.
     * @return Either the newest or oldest file.
     */
    static File getLastModified(boolean lastModified) {
        File[] files = replayDir.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        long leastModifiedTime = Long.MAX_VALUE;

        File chosenFile;
        try {
            chosenFile = files[0];
        } catch (Exception e) {
            return null;
        }

        if (lastModified) {
            for (File f : files) {
                if (f.lastModified() > lastModifiedTime) {
                    chosenFile = f;
                    lastModifiedTime = f.lastModified();
                }
            }
        } else {
            for (File f : files) {
                if (f.lastModified() < leastModifiedTime) {
                    chosenFile = f;
                    leastModifiedTime = f.lastModified();
                }
            }
        }
        return chosenFile;
    }

    static int numberOfFiles() {
        try {
            return replayDir.list().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
