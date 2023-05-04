package com.nf.mvc.support.converter;

import com.nf.mvc.support.WebTypeConverter;

public class FloatTypeConverter implements WebTypeConverter {

    @Override
    public Float convert(String paramValue) throws Exception {
        return Float.valueOf(paramValue);
    }
}
