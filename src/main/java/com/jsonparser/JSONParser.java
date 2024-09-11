package com.jsonparser;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JSONParser {
    private final List<String> tokens;
    private int position;

    public JSONParser(String json) {
        JSONTokenizer tokenizer = new JSONTokenizer(json);
        this.tokens = tokenizer.tokenize();
        this.position = 0;
    }

    public Object parse() {
        String token = tokens.get(position);
        if (token.equals("{")) {
            return parseObject();
        } else if (token.equals("[")) {
            return parseArray();
        } else {
            throw new JSONException("Invalid JSON: must start with { or [");
        }
    }

    private JSONObject parseObject() {
        JSONObject jsonObject = new JSONObject();
        position++; // Skip {

        boolean expectingComma = false;
        while (!tokens.get(position).equals("}")) {
            if (expectingComma) {
                if (!tokens.get(position).equals(",")) {
                    throw new JSONException("Expected ,");
                }
                position++;
                if (tokens.get(position).equals("}")) {
                    throw new JSONException("Trailing comma in object");
                }
            }

            String key = parseString();
            if (key.isEmpty())
                throw new JSONException("Missing key");

            position++; // Skip :
            Object value = parseValue();
            jsonObject.put(key, value);

            expectingComma = true;
        }

        position++; // Skip }
        return jsonObject;
    }

    private JSONArray parseArray() {
        JSONArray jsonArray = new JSONArray();
        position++; // Skip [

        boolean expectingComma = false;
        while (!tokens.get(position).equals("]")) {
            if (expectingComma) {
                if (!tokens.get(position).equals(",")) {
                    throw new JSONException("Expected , in array");
                }
                position++;
                if (tokens.get(position).equals("]")) {
                    throw new JSONException("Trailing comma in array");
                }
            }

            jsonArray.add(parseValue());
            expectingComma = true;
        }

        position++; // Skip ]
        return jsonArray;
    }

    private Object parseValue() {
        String token = tokens.get(position);

        if (token.equals("{")) {
            return parseObject();
        } else if (token.equals("[")) {
            return parseArray();
        } else if (token.startsWith("\"")) {
            return parseString();
        } else if (token.equals("true")) {
            position++;
            return true;
        } else if (token.equals("false")) {
            position++;
            return false;
        } else if (token.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?")) {
            return parseNumber();
        } else {
            throw new JSONException("Unexpected token: " + token);
        }
    }

    private String parseString() {
        String token = tokens.get(position);
        position++;
        if (token.length() < 2) {
            throw new JSONException("Invalid string: " + token);
        }
        return token.substring(1, token.length() - 1);
    }

    private Number parseNumber() {
        String token = tokens.get(position);
        position++;

        if (token.contains(".") || token.contains("e") || token.contains("E")) {
            return Double.parseDouble(token);
        } else {
            long value = Long.parseLong(token);
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                return (int) value;
            } else {
                return value;
            }
        }
    }

    public static JSONObject parseToJSONObject(String json) {
        JSONParser parser = new JSONParser(json);
        Object result = parser.parse();
        if (result instanceof JSONObject) {
            return (JSONObject) result;
        } else {
            throw new JSONException("Root element is not a JSONObject");
        }
    }

    public static JSONArray parseToJSONArray(String json) {
        JSONParser parser = new JSONParser(json);
        Object result = parser.parse();
        if (result instanceof JSONArray) {
            return (JSONArray) result;
        } else {
            throw new JSONException("Root element is not a JSONArray");
        }
    }

    public static Map<String, Object> parseToMap(String json) {
        JSONParser parser = new JSONParser(json);
        Object result = parser.parse();
        if (result instanceof JSONObject) {
            return ((JSONObject) result).toMap();
        } else {
            throw new JSONException("Root element is not a JSONObject");
        }
    }

    public static List<Object> parseToList(String json) {
        JSONParser parser = new JSONParser(json);
        Object result = parser.parse();
        if (result instanceof JSONArray) {
            return ((JSONArray) result).toList();
        } else {
            throw new JSONException("Root element is not a JSONArray");
        }
    }

    public static <T> T parseToClass(String json, Class<T> clazz) {
        JSONParser parser = new JSONParser(json);
        Object result = parser.parse();
        return convertToClass(result, clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertToClass(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }

        if (clazz.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }

        if (obj instanceof JSONObject) {
            return convertJSONObjectToClass((JSONObject) obj, clazz);
        }

        if (obj instanceof JSONArray) {
            return convertJSONArrayToClass((JSONArray) obj, clazz);
        }

        throw new JSONException("Cannot convert " + obj.getClass() + " to " + clazz);
    }

    private static <T> T convertJSONObjectToClass(JSONObject jsonObject, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = jsonObject.get(field.getName());

                if (value != null) {
                    if (value instanceof JSONObject) {
                        field.set(instance, convertToClass(value, field.getType()));
                    } else if (value instanceof JSONArray) {
                        field.set(instance, convertJSONArrayToField((JSONArray) value, field));
                    } else {
                        // Handle type mismatch for numbers
                        if (field.getType() == int.class && value instanceof Long) {
                            field.set(instance, ((Long) value).intValue());
                        } else if (field.getType() == long.class && value instanceof Integer) {
                            field.set(instance, ((Integer) value).longValue());
                        } else {
                            field.set(instance, value);
                        }
                    }
                }
            }

            return instance;
        } catch (ReflectiveOperationException e) {
            throw new JSONException("Error creating instance of " + clazz, e);
        }
    }

    private static <T> T convertJSONArrayToClass(JSONArray jsonArray, Class<T> clazz) {
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            Object array = java.lang.reflect.Array.newInstance(componentType, jsonArray.toList().size());
            for (int i = 0; i < jsonArray.toList().size(); i++) {
                java.lang.reflect.Array.set(array, i, convertToClass(jsonArray.get(i), componentType));
            }
            return (T) array;
        }

        if (List.class.isAssignableFrom(clazz)) {
            List<Object> list = new ArrayList<>();
            for (Object item : jsonArray.toList()) {
                list.add(item);
            }
            return (T) list;
        }

        throw new JSONException("Cannot convert JSONArray to " + clazz);
    }

    private static Object convertJSONArrayToField(JSONArray jsonArray, Field field) throws ReflectiveOperationException {
        Class<?> fieldType = field.getType();

        if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            Object array = java.lang.reflect.Array.newInstance(componentType, jsonArray.toList().size());
            for (int i = 0; i < jsonArray.toList().size(); i++) {
                java.lang.reflect.Array.set(array, i, convertToClass(jsonArray.get(i), componentType));
            }
            return array;
        }

        if (Collection.class.isAssignableFrom(fieldType)) {
            Collection<Object> collection;
            if (List.class.isAssignableFrom(fieldType)) {
                collection = new ArrayList<>();
            } else if (Set.class.isAssignableFrom(fieldType)) {
                collection = new HashSet<>();
            } else {
                throw new JSONException("Unsupported collection type: " + fieldType);
            }

            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Class<?> elementType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                for (Object item : jsonArray.toList()) {
                    collection.add(convertToClass(item, elementType));
                }
            } else {
                collection.addAll(jsonArray.toList());
            }

            return collection;
        }

        throw new JSONException("Cannot convert JSONArray to " + fieldType);
    }

}