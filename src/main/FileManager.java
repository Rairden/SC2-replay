package main;

import java.io.*;
import java.util.Arrays;

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
        if (!isModified(fullPath, file, score)) return;

        writeFile(score, file);
    }

    private void writeFile(int[] score, File f) throws IOException {
        StringBuilder sb = new StringBuilder();
        String race1 = String.format("%2s", score[0]);

        sb.append(race1).append(" - ").append(score[1]);

        FileWriter fw = new FileWriter(f);
        fw.write(sb.toString());
        fw.close();
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

    public int numberOfFiles() {
        try {
            return file.list().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
