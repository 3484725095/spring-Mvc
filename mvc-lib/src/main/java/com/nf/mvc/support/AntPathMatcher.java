package com.nf.mvc.support;


import java.util.Comparator;

/** 此类参考spring 的AntPathMatcher实现，下面是spring关于AntPathMatcher的一个说明
 * Path matcher implementation for Ant-style path patterns. This implementation matches URLs using the following rules:
 * <p/>
 * '?' - matches one character
 * '*' - matches zero or more characters
 * '**' - matches zero or more directories in a path
 * <p/>
 * The instances of this class can be configured via its {@link Builder} to:
 * (1) Use a custom path separator. The default is '/' character
 * (2) Ignore character case during comparison. The default is {@code false}
 * (3) Match start. Determines whether the pattern at least matches as far as the given base path goes,
 * assuming that a full path may then match as well. The default is {@code false}
 * <p/>
 * (4) Specify whether to trim tokenized paths. The default is {@code false}
 * The custom path separator & ignoring character case options were inspired by Spring's AntPathMatcher
 *
 * 其中match Start的含义是如果path与patter完全匹配，那么肯定是算匹配的，比如a/ * /c模式与a/b0/c
 * 但如果在设置了matchStart的情况下，pattern的前面一部分就已经匹配了整个path的话，也算匹配，比如a/ * /c/d模式与a/b0/c路径
 * 而trimToken主要是时候忽略一些空白字符的情况
 * 参考spring的PathMatcher，AntPathMatcher。现在spring 5.0有另一个新的路径处理的类PathPattern（spring 5.0才出现）
 */
public class AntPathMatcher implements PathMatcher{

    private static final char ASTERISK = '*';
    private static final char QUESTION = '?';
    private static final char BLANK = ' ';
    private static final int ASCII_CASE_DIFFERENCE_VALUE = 32;

    private final char pathSeparator;
    private final boolean ignoreCase;
    private final boolean matchStart;
    private final boolean trimTokens;

    private AntPathMatcher(final char pathSeparator, boolean ignoreCase, boolean matchStart, boolean trimTokens) {
        this.pathSeparator = pathSeparator;
        this.ignoreCase = ignoreCase;
        this.matchStart = matchStart;
        this.trimTokens = trimTokens;
    }

    public boolean isPattern( String path) {
        if (path == null) {
            return false;
        }
        boolean uriVar = false;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '*' || c == '?') {
                return true;
            }
            if (c == '{') {
                uriVar = true;
                continue;
            }
            if (c == '}' && uriVar) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMatch(final String pattern, final String path) {
        if (pattern.isEmpty()) {
            return path.isEmpty();
        } else if (path.isEmpty() && pattern.charAt(0) == pathSeparator) {
            if (matchStart) {
                return true;
            } else if (pattern.length() == 2 && pattern.charAt(1) == ASTERISK) {
                return false;
            }
            return isMatch(pattern.substring(1), path);
        }

        final char patternStart = pattern.charAt(0);
        if (patternStart == ASTERISK) {

            if (pattern.length() == 1) {
                return path.isEmpty() || path.charAt(0) != pathSeparator && isMatch(pattern, path.substring(1));
            } else if (doubleAsteriskMatch(pattern, path)) {
                return true;
            }

            int start = 0;
            while (start < path.length()) {
                if (isMatch(pattern.substring(1), path.substring(start))) {
                    return true;
                }
                start++;
            }
            return isMatch(pattern.substring(1), path.substring(start));
        }

        int pointer = skipBlanks(path);

        return !path.isEmpty() && (equal(path.charAt(pointer), patternStart) || patternStart == QUESTION)
                && isMatch(pattern.substring(1), path.substring(pointer + 1));
    }

    private boolean doubleAsteriskMatch(final String pattern, final String path) {
        if (pattern.charAt(1) != ASTERISK) {
            return false;
        } else if (pattern.length() > 2) {
            return isMatch(pattern.substring(3), path);
        }

        return false;
    }

    /*
     private boolean doubleAsteriskMatch(final String pattern, final String path) {
        if (pattern.charAt(1) != ASTERISK) {
            return false;
        } else if (pattern.length() > 2 && isMatch(pattern.substring(3), path)) {
            return true;
        }
        int pointer = 0;
        for (int idx = 0; idx < path.length(); idx++) {
            if (path.charAt(idx) == pathSeparator) {
                pointer = idx;
                break;
            }
        }
        return isMatch(pattern.substring(2), path.substring(pointer));
    }
     */

    private int skipBlanks(final String path) {
        int pointer = 0;
        if (trimTokens) {
            while (!path.isEmpty() && pointer < path.length() && path.charAt(pointer) == BLANK) {
                pointer++;
            }
        }
        return pointer;
    }

    public Comparator<String> getPatternComparator(String path) {
        return new AntPatternComparator(path);
    }

    private boolean equal(final char pathChar, final char patternChar) {
        if (ignoreCase) {
            return pathChar == patternChar ||
                    ((pathChar > patternChar) ?
                            pathChar == patternChar + ASCII_CASE_DIFFERENCE_VALUE :
                            pathChar == patternChar - ASCII_CASE_DIFFERENCE_VALUE);
        }
        return pathChar == patternChar;
    }

    public static final class Builder {

        private char pathSeparator = '/';
        private boolean ignoreCase = false;
        private boolean matchStart = false;
        private boolean trimTokens = false;

        public Builder() {

        }

        public Builder withPathSeparator(final char pathSeparator) {
            this.pathSeparator = pathSeparator;
            return this;
        }

        public Builder withIgnoreCase() {
            this.ignoreCase = true;
            return this;
        }

        public Builder withMatchStart() {
            this.matchStart = true;
            return this;
        }

        public Builder withTrimTokens() {
            this.trimTokens = true;
            return this;
        }

        public AntPathMatcher build() {
            return new AntPathMatcher(pathSeparator, ignoreCase, matchStart, trimTokens);
        }
    }

    protected static class AntPatternComparator implements Comparator<String> {

        private final String path;

        public AntPatternComparator(String path) {
            this.path = path;
        }

        /**
         * Compare two patterns to determine which should match first, i.e. which
         * is the most specific regarding the current path.
         * @return a negative integer, zero, or a positive integer as pattern1 is
         * more specific, equally specific, or less specific than pattern2.
         */
        @Override
        public int compare(String pattern1, String pattern2) {
            PatternInfo info1 = new PatternInfo(pattern1);
            PatternInfo info2 = new PatternInfo(pattern2);

            if (info1.isLeastSpecific() && info2.isLeastSpecific()) {
                return 0;
            }
            else if (info1.isLeastSpecific()) {
                return 1;
            }
            else if (info2.isLeastSpecific()) {
                return -1;
            }

            boolean pattern1EqualsPath = pattern1.equals(this.path);
            boolean pattern2EqualsPath = pattern2.equals(this.path);
            if (pattern1EqualsPath && pattern2EqualsPath) {
                return 0;
            }
            else if (pattern1EqualsPath) {
                return -1;
            }
            else if (pattern2EqualsPath) {
                return 1;
            }

            if (info1.isPrefixPattern() && info2.isPrefixPattern()) {
                return info2.getLength() - info1.getLength();
            }
            else if (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0) {
                return 1;
            }
            else if (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0) {
                return -1;
            }

            if (info1.getTotalCount() != info2.getTotalCount()) {
                return info1.getTotalCount() - info2.getTotalCount();
            }

            if (info1.getLength() != info2.getLength()) {
                return info2.getLength() - info1.getLength();
            }

            if (info1.getSingleWildcards() < info2.getSingleWildcards()) {
                return -1;
            }
            else if (info2.getSingleWildcards() < info1.getSingleWildcards()) {
                return 1;
            }

            if (info1.getUriVars() < info2.getUriVars()) {
                return -1;
            }
            else if (info2.getUriVars() < info1.getUriVars()) {
                return 1;
            }

            return 0;
        }


        /**
         * Value class that holds information about the pattern, e.g. number of
         * occurrences of "*", "**", and "{" pattern elements.
         */
        private static class PatternInfo {

            private final String pattern;

            private int uriVars;

            private int singleWildcards;

            private int doubleWildcards;

            private boolean catchAllPattern;

            private boolean prefixPattern;


            private Integer length;

            public PatternInfo( String pattern) {
                this.pattern = pattern;
                if (this.pattern != null) {
                    initCounters();
                    this.catchAllPattern = this.pattern.equals("/**");
                    this.prefixPattern = !this.catchAllPattern && this.pattern.endsWith("/**");
                }
                if (this.uriVars == 0) {
                    this.length = (this.pattern != null ? this.pattern.length() : 0);
                }
            }

            protected void initCounters() {
                int pos = 0;
                if (this.pattern != null) {
                    while (pos < this.pattern.length()) {
                        if (this.pattern.charAt(pos) == '{') {
                            this.uriVars++;
                            pos++;
                        }
                        else if (this.pattern.charAt(pos) == '*') {
                            if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == '*') {
                                this.doubleWildcards++;
                                pos += 2;
                            }
                            else if (pos > 0 && !this.pattern.substring(pos - 1).equals(".*")) {
                                this.singleWildcards++;
                                pos++;
                            }
                            else {
                                pos++;
                            }
                        }
                        else {
                            pos++;
                        }
                    }
                }
            }

            public int getUriVars() {
                return this.uriVars;
            }

            public int getSingleWildcards() {
                return this.singleWildcards;
            }

            public int getDoubleWildcards() {
                return this.doubleWildcards;
            }

            public boolean isLeastSpecific() {
                return (this.pattern == null || this.catchAllPattern);
            }

            public boolean isPrefixPattern() {
                return this.prefixPattern;
            }

            public int getTotalCount() {
                return this.uriVars + this.singleWildcards + (2 * this.doubleWildcards);
            }

            public int getLength() {
                return this.length;
            }
        }
    }
}