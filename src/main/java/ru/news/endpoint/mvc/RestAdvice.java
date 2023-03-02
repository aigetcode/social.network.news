package ru.news.endpoint.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import ru.news.exceptions.ExceptionResponse;

import java.util.Objects;

@RestControllerAdvice
public class RestAdvice implements ResponseBodyAdvice<Object> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (!(body instanceof ExceptionResponse)
                && returnType.getContainingClass().isAnnotationPresent(Api.class)
                && !Objects.requireNonNull(returnType.getMethod()).isAnnotationPresent(IgnoreResponseBinding.class)) {
            SuccessResponse response = SuccessResponse.builder()
                    .success(true)
                    .response(body)
                    .build();
            return body instanceof String ? mapper.writeValueAsString(response) : response;
        }
        return body;
    }
}