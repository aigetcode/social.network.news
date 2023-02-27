package ru.news.util;

public class Utils {

    private Utils() {
    }

    public static void required(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void state(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

}
