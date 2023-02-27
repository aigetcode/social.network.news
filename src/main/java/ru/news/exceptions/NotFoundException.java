package ru.news.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends RuntimeException {

    private final int status;
    private final String reason;

    public NotFoundException(HttpStatus status, @Nullable String reason) {
        super("");
        Assert.notNull(status, "HttpStatus is required");
        this.status = status.value();
        this.reason = reason;
    }

    public NotFoundException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        Assert.notNull(status, "HttpStatus is required");
        this.status = status.value();
        this.reason = reason;
    }

    public NotFoundException(int rawStatusCode, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        this.status = rawStatusCode;
        this.reason = reason;
    }

}

