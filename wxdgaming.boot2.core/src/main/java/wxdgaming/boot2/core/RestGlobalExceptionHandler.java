package wxdgaming.boot2.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import wxdgaming.boot2.core.lang.RunResult;

@Slf4j
@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ErrorResponse handleException(Throwable throwable) {
        if (!(throwable instanceof NoResourceFoundException)) {
            log.error("系统异常", throwable);
        }
        return ErrorResponse.create(throwable, HttpStatus.INTERNAL_SERVER_ERROR, "系统异常");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RunResult> handleRuntimeException(Exception ex) {
        log.error("运行时异常", ex);
        return ResponseEntity.ok(RunResult.fail(ex.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, ServletRequestBindingException.class})
    public ResponseEntity<RunResult> handleIllegalArgumentException(Exception ex) {
        log.warn("参数异常", ex);
        return ResponseEntity.ok(RunResult.fail(ex.getMessage()));
    }

}
