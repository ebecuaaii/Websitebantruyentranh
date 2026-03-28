package com.example.demo.util;

public class AppUtil {

    private AppUtil() {}

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
