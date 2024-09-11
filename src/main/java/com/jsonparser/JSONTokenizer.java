package com.jsonparser;

import java.util.ArrayList;
import java.util.List;

public class JSONTokenizer {
    private final String json;
    private int position;

    public JSONTokenizer(String json) {
        this.json = json;
        this.position = 0;
    }

    public List<String> tokenize() {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        while (position < json.length()) {
            char c = json.charAt(position);

            if (Character.isWhitespace(c)) {
                position++;
                continue;
            }

            if (c == '{' || c == '}' || c == '[' || c == ']' || c == ':' || c == ',') {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                tokens.add(String.valueOf(c));
                position++;
            } else if (c == '"') {
                int endQuote = findClosingQuote(position + 1);
                tokens.add(json.substring(position, endQuote + 1));
                position = endQuote + 1;
            } else if (Character.isDigit(c) || c == '-') {
                int endNumber = findEndOfNumber(position);
                tokens.add(json.substring(position, endNumber));
                position = endNumber;
            } else if (c == 't' || c == 'f' || c == 'n') {
                int endLiteral = findEndOfLiteral(position);
                tokens.add(json.substring(position, endLiteral));
                position = endLiteral;
            } else {
                throw new JSONException("Unexpected character: " + c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private int findClosingQuote(int start) {
        for (int i = start; i < json.length(); i++) {
            if (json.charAt(i) == '"' && json.charAt(i - 1) != '\\') {
                return i;
            }
        }
        throw new JSONException("Unclosed quote");
    }

    private int findEndOfNumber(int start) {
        int i = start;
        boolean dotSeen = false;
        boolean eSeen = false;

        while (i < json.length()) {
            char c = json.charAt(i);
            if (Character.isDigit(c)) {
                i++;
            } else if (c == '.' && !dotSeen) {
                dotSeen = true;
                i++;
            } else if ((c == 'e' || c == 'E') && !eSeen) {
                eSeen = true;
                i++;
                if (i < json.length() && (json.charAt(i) == '+' || json.charAt(i) == '-')) {
                    i++;
                }
            } else {
                break;
            }
        }
        return i;
    }

    private int findEndOfLiteral(int start) {
        String literal = json.substring(start, Math.min(start + 5, json.length()));
        if (literal.startsWith("true")) return start + 4;
        if (literal.startsWith("false")) return start + 5;
        if (literal.startsWith("null")) return start + 4;
        throw new JSONException("Invalid literal: " + literal);
    }
}