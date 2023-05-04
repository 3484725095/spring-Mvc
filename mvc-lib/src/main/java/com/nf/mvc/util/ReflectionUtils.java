package com.nf.mvc.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.regex.Pattern;

public abstract class ReflectionUtils {
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(9);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(9);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        primitiveWrapperTypeMap.put(Void.class, void.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static <T> T newInstance(Class<? extends T> clz) {
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Method> getAllSetterMethods(Class<?> clz) {
        List<Method> setterMethods = new ArrayList<>();
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            if (ReflectionUtils.isSetter(method)) {
                setterMethods.add(method);
            }
        }
        return setterMethods;
    }

    public static List<String> getParamNamesWithParamType(Class<?> clazz, String methodName) {
        List<String> paramNames = new ArrayList<>();
        ClassPool pool = ClassPool.getDefault();

        pool.insertClassPath(new ClassClassPath(ReflectionUtils.class));

        try {
            CtClass ctClass = pool.getCtClass(clazz.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
            // 使用javassist的反射方法的参数名
            javassist.bytecode.MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                int len = ctMethod.getParameterTypes().length;
                // 非静态的成员函数的第一个参数是this
                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                for (int i = 0; i < len; i++) {
                    paramNames.add(attr.variableName(i + pos));
                }
            }
            return paramNames;
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getParamNames(Class<?> clazz, String methodName) {
        return getParamNamesWithParamType(clazz, methodName);
    }

    /**
     * <a href="https://www.runoob.com/regexp/regexp-syntax.html">正则表达式入门教程</a>
     * <ul>
     * <li>^:表示以什么开始，^set意思就是以set开头</li>
     * <li>[A-Z] ： 表示一个区间，匹配所有大写字母，[a-z] 表示所有小写字母</li>
     * <li>句号：匹配除换行符（\n、\r）之外的任何单个字符</li>
     * <li>‘*’：匹配前面的子表达式零次或多次,在下面的例子就是前面的句号</li>
     * </ul>
     * <p>所以^set[A-Z].* 意思就是以set开头，之后跟一个大写字母，大写字母之后可以出现0个或多个字符</p>
     *
     * @param method
     * @return
     */
    public static boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(void.class) && method.getParameterTypes().length == 1 && method.getName().matches("^set[A-Z].*");
    }

    public static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[A-Z].*") && !method.getReturnType().equals(void.class)) return true;
            if (method.getName().matches("^is[A-Z].*") && method.getReturnType().equals(boolean.class)) return true;
        }
        return false;
    }

    public static boolean isListOrSet(Class<?> type) {
        return List.class.isAssignableFrom(type) ||
                Set.class.isAssignableFrom(type);
    }

    public static boolean isSimpleProperty(Class<?> type) {
        return isSimpleType(type) || isSimpleArrayType(type);
    }

    public static boolean isSimpleArrayType(Class<?> type) {
        //type.getComponentType() 方法返回该数组元素的类型，然后再通过调用 isSimpleType() 方法判断该类型是否为简单类型。
        return type.isArray() && isSimpleType(type.getComponentType());
    }

    //是否实现了Collection子接口和map的子接口
    public static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public static boolean isComplexProperty(Class<?> type) {
        return isSimpleProperty(type) == false && isCollection(type) == false;
    }

    public static boolean isSimpleType(Class<?> type) {
        return (Void.class != type && void.class != type && (isPrimitiveOrWrapper(type) || Enum.class.isAssignableFrom(type) || CharSequence.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type) || URI.class == type || URL.class == type || Locale.class == type || Class.class == type) || LocalDate.class == type || LocalDateTime.class == type);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            return (lhsType == resolvedPrimitive);
        } else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
        }
    }

    public static boolean isAssignableToAny(Class<?> lhsType, Class<?>... rhsTypes) {
        boolean isAssignable = false;
        for (Class<?> rhsType : rhsTypes) {
            isAssignable = isAssignable(rhsType, lhsType);
            if (isAssignable == true) {
                break;
            }
        }
        return isAssignable;
    }

    public static boolean isSimpleCollection(Class<?> type) {
        return isAssignable(List.class, type) || isAssignable(Set.class, type) || isAssignable(Map.class, type);
    }

    public static boolean isSimpleTypeCollection(Class<?> collectionType, Class<?> actualTypeParam) {
        return isSimpleCollection(collectionType) && isSimpleType(actualTypeParam);
    }
}
