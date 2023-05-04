package com.nf.mvc.util;

public interface ExceptionUtils {
    static Throwable getRootCause(Throwable ex) {
        Throwable cause;
        Throwable result = ex;
        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }
}
