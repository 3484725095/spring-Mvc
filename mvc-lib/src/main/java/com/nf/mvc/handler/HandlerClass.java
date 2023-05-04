package com.nf.mvc.handler;

import com.nf.mvc.util.ReflectionUtils;

public class HandlerClass {
    private Class<?> handlerClass;
    private Object handlerObject;

    public HandlerClass(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public HandlerClass(Object handlerObject) {
        this.handlerObject = handlerObject;
    }

    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    public Object getHandlerObject() {
        return handlerObject;
    }

    public String getSimpleName() {
        return handlerClass != null ? handlerClass.getSimpleName() : handlerObject.getClass().getSimpleName();
    }

    public Object getInstance() {
        if (handlerObject != null) {
            return handlerObject;
        }
        return ReflectionUtils.newInstance(handlerClass);
    }
}
