package com.nf.mvc.util;

import com.nf.mvc.support.WebTypeConverter;
import com.nf.mvc.support.converter.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class WebTypeConverterUtils {
    /**
     * 这是一个最简单的缓存实现，因为静态变量在类加载时处理，一般只加载一次，静态代码块执行之后，这个map内容就不变了
     * 这块内容一直放置在内存中，所以从map中取值时，一直直接在内存中，可以“理解”为从缓存中取
     * <p>
     * web里面的数据都是字符串类型的，要转换的类型也就那么一些固定的简单类型（simpleType），可以采用这种写死的方式，因为没有变化
     * 如果要让mvc框架支持其它新的特定类型，比如MultipartFile这样你自己创造的新类型，那么你就直接添加新的MethodArgumentResolver实现即可
     * 设置一个初始的容量，可以避免一些不必要的多次扩容
     */

    private static Map<Class<?>, WebTypeConverter> cachedConverters = new ConcurrentHashMap<>(32);

    static {
        cachedConverters.put(BigDecimal.class, new BigDecimalTypeConverter());
        cachedConverters.put(Boolean.class, new BooleanTypeConverter());
        cachedConverters.put(Boolean.TYPE, new BooleanTypeConverter());
        cachedConverters.put(Byte.class, new ByteTypeConverter());
        cachedConverters.put(Byte.TYPE, new ByteTypeConverter());
        cachedConverters.put(Character.class, new CharacterTypeConverter());
        cachedConverters.put(Character.TYPE, new CharacterTypeConverter());
        cachedConverters.put(Date.class, new DateTypeConverter());
        cachedConverters.put(Double.class, new DoubleTypeConverter());
        cachedConverters.put(Double.TYPE, new DoubleTypeConverter());
        cachedConverters.put(Float.class, new FloatTypeConverter());
        cachedConverters.put(Float.TYPE, new FloatTypeConverter());
        cachedConverters.put(Integer.class, new IntegerTypeConverter());
        cachedConverters.put(Integer.TYPE, new IntegerTypeConverter());
        cachedConverters.put(LocalDateTime.class, new LocalDateTimeTypeConverter());
        cachedConverters.put(LocalDate.class, new LocalDateTypeConverter());
        cachedConverters.put(LocalTime.class, new LocalTimeTypeConverter());
        cachedConverters.put(Long.class, new LongTypeConverter());
        cachedConverters.put(Long.TYPE, new LongTypeConverter());
        cachedConverters.put(Short.class, new ShortTypeConverter());
        cachedConverters.put(Short.TYPE, new ShortTypeConverter());
        cachedConverters.put(String.class, new StringTypeConverter());
    }

    /**
     * @param paramType
     * @return 返回null表示没有此类型的类型转换器
     */
    public static WebTypeConverter getTypeConverter(Class<?> paramType) {
        return cachedConverters.get(paramType);
    }


    public static <T> Object toSimpleTypeValue(Class<T> paramType, String requestParamValue) throws Exception {
        WebTypeConverter typeConverter = getTypeConverter(paramType);

        if (typeConverter == null) {
            throw new UnsupportedOperationException("不支持对此类型" + paramType.getName() + "的类型转换");
        }
        return typeConverter.convert(requestParamValue);

    }

    public static Object toSimpleTypeArray(Class<?> componentType, String[] requestParamValues) throws Exception {
/*
        if (ReflectionUtils.isSimpleTypeArray(paramType, paramGenericType) == false) {
            throw new UnsupportedOperationException("不支持对此泛型类型为:" + paramType.getName() + " 其实参类型为："+ paramGenericType +  "的类型转换");
        }*/
        //不要写这样的代码，因为实例化后可能是一个int[]或者double[],是不能转换为Object[]的
        //Object[] values = (Object[]) Array.newInstance(componentType, requestParamValues.length);
        Object array = Array.newInstance(componentType, requestParamValues.length);
        for (int i = 0; i < requestParamValues.length; i++) {
            Array.set(array, i, toSimpleTypeValue(componentType, requestParamValues[i]));
        }
        return array;
    }

    /**
     * 只处理了List与Set集合，其它集合暂不支持
     *
     * @param paramType        参数类型，比如List<String>,List<Integer>这样的类型
     * @param paramGenericType :泛型实参类型，这里只处理简单类型，不处理复杂类型
     * @return
     * @throws Exception
     */
    public static <T> Collection<T> toCollection(Class<?> paramType, Class<T> paramGenericType, String[] requestParamValues) throws Exception {

        if (ReflectionUtils.isSimpleTypeCollection(paramType, paramGenericType) == false) {
            throw new UnsupportedOperationException("不支持对此泛型类型为:" + paramType.getName() + " 其实参类型为：" + paramGenericType + "的类型转换");
        }

        Collection<T> collection = null;
        Object array = toSimpleTypeArray(paramGenericType, requestParamValues);
        if (List.class.isAssignableFrom(paramType)) {
            collection = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(paramType)) {
            collection = new HashSet<>();
        }
        for (int i = 0; i < requestParamValues.length; i++) {

            collection.add((T) Array.get(array, i));
        }
        return collection;
    }

}
