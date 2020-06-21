package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Settings {

    static Map<String, String> paths;
    static String DIR_SCORES;
    static String DIR_REPLAYS;
    static String PLAYER;
    static String PATH_PYTHON;
    static String PATH_SCRIPT;

    public Settings() throws IOException {
        InputStream is = getClass().getResourceAsStream("resources/settings.cfg");
        BufferedReader cfgTemplate = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        File userCfg = new File(System.getProperty("user.dir") + File.separator + "settings.cfg");   // creates settings.cfg in jar execution dir

        InputStream script = getClass().getResourceAsStream("resources/printReplayShort.py");
        BufferedReader br = new BufferedReader(new InputStreamReader(script, StandardCharsets.UTF_8));
        File pythonScript = new File(System.getProperty("user.dir") + File.separator + "printReplayShort.py");

        paths = new HashMap<>();
        loadCfg(cfgTemplate, userCfg);
        createPythonScript(br, pythonScript);

        DIR_SCORES  = setPath("scores");
        DIR_REPLAYS = setPath("replays");
        PLAYER      = setPath("player");
        PATH_PYTHON = setPath("python");
        PATH_SCRIPT = pythonScript.toString();
    }

    private String setPath(String type) {
        if (paths.get(type) == null && type.equals("scores")) {
            return System.getProperty("user.dir") + File.separator;
        }
        return paths.get(type);
    }

    private void createPythonScript(BufferedReader br, File pythonScript) throws IOException {
        if (pythonScript.exists()) return;
        copyFile(br, pythonScript);
    }

    private void loadCfg(BufferedReader cfgTemplate, File userCfg) throws IOException {
        Scanner scan;
        if (userCfg.exists()) {
            scan = new Scanner(userCfg);
        } else {
            copyFile(cfgTemplate, userCfg);
            System.out.println("Now set up your settings.cfg file. Then restart the program.\n");
            System.exit(0);
            return;
        }

        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            // skip comments '#', sections [] or empty lines
            if (line.matches("^#.*|\\[.*|\\s*")) continue;

            storeSettings(line);
        }

        if (paths.get("replays") == null || paths.get("player") == null || paths.get("python") == null) {
            throw new IllegalStateException("The minimum configuration is to populate: replays=, player=, python=");
        }
    }

    private void storeSettings(String line) {
        String[] keyVal = line.trim().split("=");

        if (!(keyVal.length == 2)) return;

        paths.put(keyVal[0], keyVal[1]);
    }

    private void copyFile(BufferedReader br, File userCfg) throws IOException {
        String str = "";
        StringBuilder sb = new StringBuilder();

        while ((str = br.readLine()) != null) {
            sb.append(str).append("\n");
        }
        FileWriter fw = new FileWriter(userCfg);
        fw.write(sb.toString());
        fw.close();
    }

    // can't get this to work with forward/backslash or double backslash in settings.cfg (FileNotFoundException)
    private void copyFile(InputStream src, File dest) throws IOException {
        Files.copy(src, dest.toPath());
    }
}
