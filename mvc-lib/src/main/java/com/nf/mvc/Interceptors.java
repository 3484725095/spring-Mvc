package com.nf.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解修饰在拦截器上，用来指定要拦截的地址与不进行拦截的地址
 * 默认采用ant地址模式
 *
 * <p>通过value属性指定要拦截的地址，默认值是拦截所有的地址<br/>
 * 通过excludePattern属性指定不拦截的地址，默认值是空，意思就是没有要排除的地址
 * </p>
 *
 * <p>如果includePattern与excludePattern设置有冲突以排除设置为准</p>
 *
 * @see HandlerInterceptor
 * @see com.nf.mvc.support.AntPathMatcher
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptors {
    String[] value() default {"/**"};

    String[] excludePattern() default {""};
}
