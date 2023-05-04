package com.nf.mvc.util;

public class UriUtils {
    private UriUtils() {
    }

    private static final String SUFFIX = "Controller";

    public static String generateHandleUrl(String simpleName) {
        return "/" + simpleName.substring(0, simpleName.length() - SUFFIX.length());
    }
}
