package org.sindria.xpipe.lib.nanoREST.serializers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonSerializer {

    public static String toJson(Object obj) {
        return toJson(obj, 0);
    }

    private static String toJson(Object obj, int indentLevel) {
        if (obj == null) {
            return "null";
        }

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        Field[] fields = obj.getClass().getDeclaredFields();
        List<String> fieldJsonList = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue; // Ignore static fields
                }

                String fieldName = field.getName();
                Object fieldValue = field.get(obj);

                // Convert camelCase field to snake_case
                fieldName = convertCamelToSnake(fieldName);

                fieldJsonList.add(indent(indentLevel + 1) + "\"" + fieldName + "\": " + serializeValue(fieldValue, indentLevel + 1));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        jsonBuilder.append(String.join(",\n", fieldJsonList));
        jsonBuilder.append("\n").append(indent(indentLevel)).append("}");

        return jsonBuilder.toString();
    }

    private static String serializeValue(Object value, int indentLevel) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
        if (value instanceof Boolean || value instanceof Number) {
            return value.toString();
        }
        if (value instanceof List<?> || value instanceof Set<?>) {
            return serializeCollection((Collection<?>) value, indentLevel);
        }
        if (value instanceof Map<?, ?>) {
            return serializeMap((Map<?, ?>) value, indentLevel);
        }
        return toJson(value, indentLevel); // Recursively serialize nested objects
    }

    private static String serializeCollection(Collection<?> collection, int indentLevel) {
        StringBuilder jsonArray = new StringBuilder("[\n");
        List<String> elements = new ArrayList<>();

        for (Object item : collection) {
            elements.add(indent(indentLevel + 1) + serializeValue(item, indentLevel + 1));
        }

        jsonArray.append(String.join(",\n", elements));
        jsonArray.append("\n").append(indent(indentLevel)).append("]");
        return jsonArray.toString();
    }

    private static String serializeMap(Map<?, ?> map, int indentLevel) {
        StringBuilder jsonObject = new StringBuilder("{\n");
        List<String> entries = new ArrayList<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = escapeJson(convertCamelToSnake(entry.getKey().toString()));
            entries.add(indent(indentLevel + 1) + "\"" + key + "\": " + serializeValue(entry.getValue(), indentLevel + 1));
        }

        jsonObject.append(String.join(",\n", entries));
        jsonObject.append("\n").append(indent(indentLevel)).append("}");
        return jsonObject.toString();
    }

    private static String convertCamelToSnake(String camelCase) {
        Matcher matcher = Pattern.compile("([a-z])([A-Z])").matcher(camelCase);
        return matcher.replaceAll("$1_$2").toLowerCase();
    }

    private static String indent(int level) {
        return "  ".repeat(level); // Two spaces per indent level
    }

    private static String escapeJson(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t");
    }
}

