package com.bezman.resolver;

import com.bezman.annotation.AuthedUser;
import com.bezman.model.User;
import lombok.NoArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Handler to resolve the user from the PreAuthorization class
 */
@Component
@NoArgsConstructor
public class UserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(User.class) && methodParameter.getParameterAnnotation(AuthedUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        ServletWebRequest webRequest = (ServletWebRequest) nativeWebRequest;

        System.out.println(webRequest.getRequest().getAttribute("user"));

        return webRequest.getRequest().getAttribute("user");
    }
}
