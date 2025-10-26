package com.albymens.note_app.exception;

import com.albymens.note_app.dto.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult> methodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResult(
                false, "", errors
        ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResult> duplicateResourceException(DuplicateResourceException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResult(false, ex.getMessage(), null)
        );
    }

    @ExceptionHandler(IllegalArgumentsException.class)
    public ResponseEntity<ApiResult> illegalArgumentsException(IllegalArgumentsException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResult(false, exception.getMessage(), null)
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResult> resourceNotFoundException(ResourceNotFoundException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResult(false, exception.getMessage(), null)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<String> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
    }
}
