package io.localhost.freelancer.statushukum.model.util.helper;

public class IntegerHelper {
    public static int parseIntOrDefault(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return defaultVal;
        }
    }
}
