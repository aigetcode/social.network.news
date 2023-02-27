package ru.news.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse<T> {

    private final Boolean success;
    private final T error;

    @JsonCreator
    public ExceptionResponse(@JsonProperty("success") Boolean success,
                             @JsonProperty("error") T error) {
        this.success = success;
        this.error = error;
    }

    public static <T> ExceptionResponse<T> from(Throwable ex, T body) {
        if (ex == null) {
            return null;
        }

        return new ExceptionResponse<>(false, body);
    }

}
