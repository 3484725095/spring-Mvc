package com.nf.mvc.support;

import java.nio.file.Path;

public class EqualPathMatcher implements PathMatcher {
    @Override
    public boolean isMatch(String pattern, String path) {
        return path.equals(pattern);
    }
}
