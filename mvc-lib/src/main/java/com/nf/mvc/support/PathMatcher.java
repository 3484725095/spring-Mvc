package com.nf.mvc.support;

public interface PathMatcher {
    boolean isMatch(final String pattern, final String path);
}
