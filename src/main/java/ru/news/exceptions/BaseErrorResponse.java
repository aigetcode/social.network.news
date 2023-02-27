package ru.news.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseErrorResponse {

    private String message;
    private Date time;

    public BaseErrorResponse(String message) {
        this.message = message;
        this.time = new Date();
    }
}
