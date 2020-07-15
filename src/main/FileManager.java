package main;

import java.io.*;
import java.util.Arrays;

import static main.Main.*;
import static main.Settings.DIR_REPLAYS;

public class FileManager {

    File file;
    int numFiles;

    public FileManager() {
        file = new File(DIR_REPLAYS);
        numFiles = numberOfFiles();
    }

    public void save(String fullPath, int[] score) throws IOException {
        File file = new File(fullPath);

        if (reset) {
            writeFile(score, file);
            return;
        }

        // if (!isModified(fullPath, file, score)) return;      // (Index 2 out of bounds for length 1)

        String caller = Thread.currentThread().getStackTrace()[2].getMethodName();

        if (caller.equals("saveFile")) {
            writeFile(score, file);
        } else if (caller.equals("saveMMR")) {
            writeMMR(score);
            writeMMRDiff(score, file);
        }
    }

    private void writeFile(int[] score, File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        String race1 = String.format("%2s", score[0]);

        sb.append(race1).append(" - ").append(score[1]);

        FileWriter fw = new FileWriter(f);
        fw.write(sb.toString());
        fw.close();
    }

    private void writeMMR(int[] mmr) throws IOException {
        File f = new File(System.getProperty("user.dir") + File.separator + "MMR.txt");
        FileWriter fw = new FileWriter(f);

        fw.write(mmr[0] + "\n");
        fw.close();
    }

    private void writeMMRDiff(int[] mmr, File f) throws IOException {
        if (mmr[0] == 0) return;
        int difference = startMMR - mmr[0];
        String result = "";

        FileWriter fw = new FileWriter(f);

        if (difference <= 0) {
            difference *= -1;
            result = String.format("+%s MMR", difference);
        } else {
            result = String.format("-%s MMR", difference);
        }

        fw.write(result + "\n");
        fw.close();
        writeMMR(mmr);
    }

    /**
     * There's no need to write to disk if the data has not changed.
     *
     * @return false if the existing file matches the data that has been parsed.
     * @throws IOException If an I/O error occurs
     */
    private boolean isModified(String fullPath, File file, int[] score) throws IOException {
        if (file.length() > 0) {
            BufferedReader br = new BufferedReader(new FileReader(fullPath));
            String str;

            while ((str = br.readLine()) != null) {
                String[] strArr = str.trim().split("\\s");
                int[] arr = {Integer.parseInt(strArr[0]), Integer.parseInt(strArr[2])};

                if (Arrays.hashCode(arr) == Arrays.hashCode(score)) return false;
            }
        }
        return true;
    }

    // I only need to regex match the most recent file, not all 1000 files.
    public File getLastModified() {
        File[] files = this.file.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;

        File chosenFile;
        try {
            chosenFile = files[0];
        } catch (Exception e) {
            return null;
        }
        for (File f : files) {
            if (f.lastModified() > lastModifiedTime) {
                chosenFile = f;
                lastModifiedTime = f.lastModified();
            }
        }
        return chosenFile;
    }

    public int numberOfFiles() {
        try {
            return file.list().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
