package com.nf.mvc.support;

public class EqualIgnoreCasePathMatcher implements PathMatcher{
    @Override
    public boolean isMatch(String pattern, String path) {
        return path.equalsIgnoreCase(pattern);
    }
}
