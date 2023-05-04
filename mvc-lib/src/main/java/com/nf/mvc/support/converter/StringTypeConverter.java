package com.nf.mvc.support.converter;

import com.nf.mvc.support.WebTypeConverter;

public class StringTypeConverter implements WebTypeConverter{

    @Override
    public String convert(String paramValue) throws Exception {
        return paramValue;
    }
}
