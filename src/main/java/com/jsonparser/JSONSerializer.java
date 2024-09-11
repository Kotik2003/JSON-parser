package com.jsonparser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class JSONSerializer {

    public static String serialize(Object obj) {
        return serialize(obj, new HashSet<>());
    }

    private static String serialize(Object obj, Set<Object> visited) {
        if (obj == null) {
            return "null";
        }

        if (visited.contains(obj)) {
            throw new JSONException("Cyclic dependency detected");
        }
        visited.add(obj);

        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Collection) {
            return serializeCollection((Collection<?>) obj, visited);
        }
        if (obj.getClass().isArray()) {
            return serializeArray(obj, visited);
        }
        if (obj instanceof Map) {
            return serializeMap((Map<?, ?>) obj, visited);
        }

        return serializeObject(obj, visited);
    }

    private static String serializeObject(Object obj, Set<Object> visited) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;

        for (Field field : fields) {
            field.setAccessible(true);
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("\"").append(field.getName()).append("\":");
            try {
                sb.append(serialize(field.get(obj), new HashSet<>(visited)));
            } catch (IllegalAccessException e) {
                throw new JSONException("Error accessing field: " + field.getName(), e);
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private static String serializeCollection(Collection<?> collection, Set<Object> visited) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;

        for (Object item : collection) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(serialize(item, new HashSet<>(visited)));
        }

        sb.append("]");
        return sb.toString();
    }

    private static String serializeArray(Object array, Set<Object> visited) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(serialize(Array.get(array, i), new HashSet<>(visited)));
        }

        sb.append("]");
        return sb.toString();
    }

    private static String serializeMap(Map<?, ?> map, Set<Object> visited) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("\"").append(entry.getKey().toString()).append("\":");
            sb.append(serialize(entry.getValue(), new HashSet<>(visited)));
        }

        sb.append("}");
        return sb.toString();
    }

    private static String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}