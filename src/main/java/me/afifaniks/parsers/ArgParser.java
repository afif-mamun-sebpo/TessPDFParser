package me.afifaniks.parsers;

import java.util.ArrayList;
import java.util.HashMap;

public class ArgParser {
    public static HashMap<String, Object> parseCMDArgs (String[] args) {
        HashMap<String, Object> argsMap = new HashMap<>();

        for (int i = 1; i < args.length; i ++) {
            String[] argSplit = args[i].split("=");
            String key = argSplit[0].replaceAll("^-", "");
            String value = argSplit[1];

            if (key.equals("dpi")) {
                argsMap.put(key, Integer.parseInt(value));
            } else if (key.equals("toFile")) {
                argsMap.put(key, value.equals("true"));
            } else if (key.equals("consoleOut")) {
                argsMap.put(key, value.equals("true"));
            } else {
                argsMap.put(key, value);
            }
        }
        return argsMap;
    }

    public static ArrayList<Integer> pageParser (String arg) {
        ArrayList<Integer> pages = new ArrayList<>();
        String[] indexes = arg.split(",");

        try {
            for (String index: indexes) {
                if (index.contains("-")) {
                    String[] range = index.split("-");
                    int lower = Integer.valueOf(range[0].trim());
                    int higher = Integer.valueOf(range[1].trim());

                    for (int i = lower; i <= higher; i++) {
                        pages.add(i);
                    }
                } else {
                    pages.add(Integer.valueOf(index.trim()));
                }
            }
        } catch (Exception e) {
            System.out.println("[PARSER ERROR]: Error occurred while parsing pages.");
        }

        return pages;
    }
}
