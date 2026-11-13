package com.irfan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex){

        Map<String,Object> error =  new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err-> error.put(err.getField(),err.getDefaultMessage()));
        Map<String,Object> map =  Map.of("Error","Validation failed","message",ex.getMessage());
        return ResponseEntity.badRequest().body(map);
    }


    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> hadleResourceAlreadyExist(ResourceAlreadyExistException ex){

        Map<String,Object> map =  Map.of("Error","Conflict","message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> hadleResourceNotFound(ResourceNotFoundException ex){

        Map<String,Object> map =  Map.of("Error","Not Found","message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadApiRequest(BadApiRequestException ex){

        Map<String,Object> map =  Map.of("Error","Bad Request","message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
}
