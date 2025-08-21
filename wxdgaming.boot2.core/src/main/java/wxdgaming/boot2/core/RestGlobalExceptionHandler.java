package wxdgaming.boot2.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wxdgaming.boot2.core.lang.AssertException;

@Slf4j
@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex) {
        if (ex instanceof AssertException) {
            return ErrorResponse.create(ex, HttpStatus.OK, ex.getMessage());
        }
        log.error("系统异常", ex);
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "系统异常");
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        if (ex instanceof AssertException) {
            return ErrorResponse.create(ex, HttpStatus.OK, ex.getMessage());
        }
        log.error("运行时异常", ex);
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "系统异常");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("参数异常", ex);
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "系统异常");
    }
}
