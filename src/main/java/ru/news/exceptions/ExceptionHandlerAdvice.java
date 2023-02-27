package ru.news.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<BaseErrorResponse>> handle(Throwable ex) {
        log.error("Exception handling and serialization: " + ex.getMessage(), ex);
        BaseErrorResponse baseErrorResponse = new BaseErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.from(ex, baseErrorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse<BaseErrorResponse>> handle(IllegalArgumentException ex) {
        log.error("Exception wrong arguments: " + ex.getMessage(), ex);
        BaseErrorResponse baseErrorResponse = new BaseErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.from(ex, baseErrorResponse));
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ExceptionResponse<BaseErrorResponse>> handle(Exception ex) {
        log.error("Not found exception arguments: " + ex.getMessage(), ex);
        BaseErrorResponse baseErrorResponse = new BaseErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.from(ex, baseErrorResponse));
    }

}
