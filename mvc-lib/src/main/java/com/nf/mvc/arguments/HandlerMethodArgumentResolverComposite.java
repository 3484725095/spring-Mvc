package com.nf.mvc.arguments;

import com.nf.mvc.MethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMethodArgumentResolverComposite implements MethodArgumentResolver {

    private final List<MethodArgumentResolver> argumentResolvers = new ArrayList<>();

    private final Map<MethodParameter, MethodArgumentResolver> resolverCache = new ConcurrentHashMap<>(256);

    public HandlerMethodArgumentResolverComposite addResolver(MethodArgumentResolver resolver) {
        argumentResolvers.add(resolver);
        return this;
    }

    public HandlerMethodArgumentResolverComposite addResolvers(MethodArgumentResolver... resolvers) {
        if (resolvers != null) {
            Collections.addAll(this.argumentResolvers, resolvers);
        }
        return this;
    }

    public List<MethodArgumentResolver> getResolvers() {
        return Collections.unmodifiableList(this.argumentResolvers);
    }

    public void clear() {
        this.argumentResolvers.clear();
    }


    public HandlerMethodArgumentResolverComposite addResolvers(List<MethodArgumentResolver> resolvers) {
        if (resolvers != null) {
            this.argumentResolvers.addAll(resolvers);
        }
        return this;
    }


    @Override
    public boolean supports(MethodParameter parameter) {
        return getArgumentResolver(parameter) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HttpServletRequest req) throws Exception {
        MethodArgumentResolver argumentResolver = getArgumentResolver(parameter);
        if (argumentResolver == null) {
            throw new IllegalArgumentException("不支持的参数类型 [" + parameter.getParameterType() + "]. supportsParameter 方法应该先调用");
        }
        return argumentResolver.resolveArgument(parameter, req);
    }

    /**
     * 如果有我们就直接返回，没有去总的里面找，找到了返回出去，并添加到缓存中，方便下次找的时候就直接在缓存找了
     *
     * @param parameter
     * @return
     */
    private MethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        MethodArgumentResolver result = resolverCache.get(parameter);
        if (result == null) {
            for (MethodArgumentResolver argumentResolver : argumentResolvers) {
                if (argumentResolver.supports(parameter)) {
                    result = argumentResolver;
                    resolverCache.put(parameter, argumentResolver);
                    break;
                }
            }
        }
        return result;
    }
}
