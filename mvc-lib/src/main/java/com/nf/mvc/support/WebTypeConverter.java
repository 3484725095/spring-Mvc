package com.nf.mvc.support;

public interface WebTypeConverter {
   <T> T convert(String paramValue) throws Exception;

}