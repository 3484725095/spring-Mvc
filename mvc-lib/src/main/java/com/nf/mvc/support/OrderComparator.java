package com.nf.mvc.support;

import java.util.Comparator;

public class OrderComparator<T> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        int o1Value = getOrderValue(o1);
        int o2Value = getOrderValue(o2);
        return o1Value - o2Value;
    }

    private static int getOrderValue(Object obj) {
        return obj.getClass().isAnnotationPresent(Order.class)
                ? obj.getClass().getDeclaredAnnotation(Order.class).value()
                : Integer.MAX_VALUE;
    }
}
