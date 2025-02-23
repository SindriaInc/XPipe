package org.sindria.xpipe.lib.nanoREST.helpers;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.Map;

public class YamlHelper {
    private static final Yaml yaml;

    static {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    private YamlHelper() {
        // Private constructor to prevent instantiation
    }

    public static <T> T loadFromString(String yamlContent, Class<T> type) {
        return yaml.loadAs(yamlContent, type);
    }

    public static <T> T loadFromFile(File file, Class<T> type) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return yaml.loadAs(reader, type);
        }
    }

    public static <T> void saveToFile(File file, T data) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
        }
    }

    public static String saveToString(Object data) {
        return yaml.dump(data);
    }

    public static Map<String, Object> loadAsMap(String yamlContent) {
        return yaml.load(yamlContent);
    }

//    public static void main(String[] args) {
//        String yamlData = "name: John Doe\nage: 30\ncity: New York";
//
//        // Load YAML as Map
//        Map<String, Object> map = YamlHelper.loadAsMap(yamlData);
//        System.out.println("Loaded YAML as Map: " + map);
//
//        // Convert Map back to YAML string
//        String yamlString = YamlHelper.saveToString(map);
//        System.out.println("YAML String:\n" + yamlString);
//    }

}

