package com.nf.mvc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * 在https://juejin.cn/post/6844904166809157639有对jackson的入门教程
 * https://www.baeldung.com/jackson-serialize-dates(有关于日期的处理教程）
 * https://www.baeldung.com/jackson
 */
public class JacksonUtils {
    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        //需要在pom中添加jackson-datatype-jsr310依赖，以便让其支持jdk8的相关时间API的支持
        objectMapper.registerModule(new JavaTimeModule());
        //不把日期序列化为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //设置日期序列化格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //不对null值进行序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //json文本的数据比pojo类多了一些内容，不报错，忽略这些多余的位置数据
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static <T> T fromJson(final InputStream inputStream, Class<T> type) {
        T data;
        try {
            data = getObjectMapper().readValue(inputStream, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("反序列化失败，是否数据格式不对?", e);
        }
        return data;
    }

    public static <T> T fromJson(final InputStream inputStream, final JavaType type) {
        T data;
        try {
            data = getObjectMapper().readValue(inputStream, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("反序列化失败，是否数据格式不对?", e);
        }
        return data;
    }

    public static <T> T fromJson(final InputStream inputStream, final TypeReference<T> type) {
        T data;
        try {
            data = getObjectMapper().readValue(inputStream, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("反序列化失败，是否数据格式不对?", e);
        }
        return data;
    }

    public static <T> T fromJson(final String json, final TypeReference<T> type) {
        T data;
        try {
            data = getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("反序列化失败，是否数据格式不对?", e);
        }
        return data;
    }

    public static String toJson(Object obj) {
        String json = null;
        try {
            json = getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("序列化失败", e);
        }
        return json;
    }
}
