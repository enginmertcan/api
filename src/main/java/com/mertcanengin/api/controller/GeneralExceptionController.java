package com.mertcanengin.api.controller;

import com.mertcanengin.api.common.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionController {
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<ErrorMessage> exception (GeneralException exception){
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

@Getter
@Setter
@AllArgsConstructor
 class ErrorMessage{
    private String errorMessage;
}
