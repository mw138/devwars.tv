package com.bezman.resolver;

import com.bezman.Reference.Reference;
import com.bezman.annotation.JSONParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * Resolver to turn a JSON object into a java object
 */
public class JSONParamResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(JSONParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = ((ServletWebRequest) nativeWebRequest).getRequest();

        JSONParam param = methodParameter.getParameterAnnotation(JSONParam.class);

        return Reference.objectMapper.readValue(request.getParameter(param.value()), methodParameter.getParameterType());
    }
}
