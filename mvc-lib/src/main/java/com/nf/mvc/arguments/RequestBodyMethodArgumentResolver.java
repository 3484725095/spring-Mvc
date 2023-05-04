package com.nf.mvc.arguments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nf.mvc.MethodArgumentResolver;
import com.nf.mvc.util.JacksonUtils;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static com.nf.mvc.util.JacksonUtils.fromJson;
import static com.nf.mvc.util.ReflectionUtils.isListOrSet;

public class RequestBodyMethodArgumentResolver implements MethodArgumentResolver {
    /**
     * 判断参数上是否有这个注解，如果有就支持，没有不支持
     *
     * @param parameter
     * @return
     */
    @Override
    public boolean supports(MethodParameter parameter) {
        return parameter.getParameter().isAnnotationPresent(RequestBody.class);
    }

    /**
     * 这个方法其实本质意思就是为了处理list,set这种集合里面的参数
     * 这个方法大概得思路就是通过参数类型去匹配是不是List集合，或者Set集合,如果是进行处理，如果不是就正常处理
     * 如果是的话，那我们就要获取到Parameter的ParameterizedType这个对象，该对象表示一个参数化类型，即形如List<String>、Map<Integer, String>之类的类型
     * 在通过该对象获取到实际的参数，比如List<Emp>我们得到的就是emp类型,最后通过Jackson的一个方法帮我们进行实例化
     *
     * @param parameter
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest req) throws Exception {
        //首先获取参数类型
        Class<?> parameterType = parameter.getParameterType();
        //通过一个方法判断这个参数类型是List或者Set类型
        if (isListOrSet(parameterType)) {
            //通过获取参数最终获取到ParameterizedType该对象表示一个参数化类型，即形如List<String>、Map<Integer, String>之类的类型。
            ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameter().getParameterizedType();
            //获取到实际的参数比如说List<Emp>那么我们获取的就是emp [0]代表第一个
            Class<?> actualTypeArguments = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            CollectionType collectionType = null;
            if (parameterType == List.class) {
                //通过jackson的一个内部工具自动给我们实例化一个集合，就相当于new ArrayList();
                collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, actualTypeArguments);
            } else if (Set.class == parameterType) {
                collectionType = TypeFactory.defaultInstance().constructCollectionType(Set.class, actualTypeArguments);
            }
            //最终用静态引入把数据返回
            return fromJson(req.getInputStream(), collectionType);
        } else {
            return fromJson(req.getInputStream(), parameterType);
        }
    }
}
