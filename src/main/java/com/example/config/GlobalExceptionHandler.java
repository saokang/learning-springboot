package com.example.config;

import com.example.exception.BusinessException;
import com.example.exception.UnknownException;
import com.example.util.LogUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UnknownException.class)
    public ResponseEntity<String> handleUnknownException(UnknownException exception) {
        LogUtils.error(exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException exception) {
        LogUtils.error(exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        LogUtils.error(exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
