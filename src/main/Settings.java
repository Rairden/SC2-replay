package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static main.Main.currentMMR;
import static main.Main.startMMR;

public class Settings {

    static Map<String, String> paths;
    static String DIR_REPLAYS;
    static String PLAYER;
    static String PATH_PYTHON;
    static String PATH_SCRIPT;
    static String userDir = System.getProperty("user.dir") + File.separator;

    public Settings() throws IOException {
        InputStream is = getClass().getResourceAsStream("resources/settings.cfg");
        BufferedReader cfgTemplate = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        File userCfg = new File(userDir + "settings.cfg");

        InputStream script = getClass().getResourceAsStream("resources/printReplayShort.py");
        BufferedReader br = new BufferedReader(new InputStreamReader(script, StandardCharsets.UTF_8));
        File pythonScript = new File(userDir + "printReplayShort.py");

        paths = new HashMap<>();
        loadCfg(cfgTemplate, userCfg);
        createPythonScript(br, pythonScript);

        DIR_REPLAYS = paths.get("replays");
        PLAYER      = paths.get("player");
        PATH_PYTHON = paths.get("python");
        PATH_SCRIPT = pythonScript.toString();
    }

    void createPythonScript(BufferedReader br, File pythonScript) throws IOException {
        if (pythonScript.exists()) return;
        copyFile(br, pythonScript);
    }

    void loadCfg(BufferedReader cfgTemplate, File userCfg) throws IOException {
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

    void storeSettings(String line) {
        String[] keyVal = line.trim().split("=");

        if (!(keyVal.length == 2)) return;

        paths.put(keyVal[0], keyVal[1]);
    }

    void loadMMR() throws IOException {
        File mmrFile = new File(userDir + "MMR.txt");
        if (mmrFile.exists()) {
            Scanner scan = new Scanner(mmrFile);
            currentMMR = startMMR = Integer.parseInt(scan.nextLine());
        } else {
            FileWriter fw = new FileWriter(mmrFile);
            fw.write("0\n");
            fw.close();
        }
    }

    void copyFile(BufferedReader br, File userCfg) throws IOException {
        String str = "";
        StringBuilder sb = new StringBuilder();

        while ((str = br.readLine()) != null) {
            sb.append(str).append("\n");
        }
        FileWriter fw = new FileWriter(userCfg);
        fw.write(sb.toString());
        fw.close();
    }
}
