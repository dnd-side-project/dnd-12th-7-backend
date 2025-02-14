package com.dnd.moddo.global.exception;

import com.dnd.moddo.global.logging.LoggingUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ModdoException.class})
    public ResponseEntity<ErrorResponse> handleDefineException(ModdoException exception) {
        LoggingUtils.warn(exception);

        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorResponse(exception.getStatus().value(), exception.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleDefineException(MethodArgumentNotValidException exception) {
        LoggingUtils.warn(exception);

        String message;

        if (exception.getFieldError() == null) {
            message = "";
        } else {
            message = exception.getFieldError().getDefaultMessage();
        }

        return ResponseEntity.status(400)
                .body(new ErrorResponse(400, message));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleDefineException(RuntimeException exception) {
        LoggingUtils.error(exception);

        return ResponseEntity.status(500)
                .body(new ErrorResponse(500, "서버에서 알 수 없는 에러가 발생했습니다."));
    }
}
