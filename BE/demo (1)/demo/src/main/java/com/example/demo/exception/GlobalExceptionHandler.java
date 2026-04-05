package com.example.demo.exception;

import com.example.demo.dto.response.BaseResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BaseResponse<Void> handleDuplicateKey(DuplicateKeyException ex) {
        return BaseResponse.error(409, "Email hoặc username đã tồn tại");
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BaseResponse<Void> handleConflict(IllegalStateException ex) {
        return BaseResponse.error(409, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleBadRequest(IllegalArgumentException ex) {
        return BaseResponse.error(400, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleRuntime(RuntimeException ex) {
        return BaseResponse.error(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleGeneral(Exception ex) {
        return BaseResponse.error(400, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().isEmpty()
            ? "Validation error"
            : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return BaseResponse.error(400, message);
    }
}
