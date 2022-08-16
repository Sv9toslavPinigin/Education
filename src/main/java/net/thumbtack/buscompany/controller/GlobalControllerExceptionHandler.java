package net.thumbtack.buscompany.controller;


import net.thumbtack.buscompany.dto.response.ErrorDto;
import net.thumbtack.buscompany.dto.response.MyError;
import net.thumbtack.buscompany.exception.BuscompanyException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorDto handleFieldValidation(MethodArgumentNotValidException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getFieldErrors().stream()
                .map((x -> new MyError("VALIDATION_FAILED", x.getField(), x.getDefaultMessage())))
                .collect(Collectors.toList()));
        return errorDto;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BuscompanyException.class)
    @ResponseBody
    public ErrorDto handleBuscompanyException(BuscompanyException ex) {
        return new ErrorDto(List.of(new MyError(ex.getErrorCode().toString(),
                ex.getField(),
                ex.getErrorCode().getErrorString())));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDto errorHandler(ConstraintViolationException ex) {
        ErrorDto errorDto = new ErrorDto(
                ex.getConstraintViolations().stream()
                        .map(x -> new MyError("VALIDATION_FAILED", x.getPropertyPath().toString(), x.getMessage()))
                        .collect(Collectors.toList()));
        return errorDto;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ErrorDto handleOptimisticLockException(ObjectOptimisticLockingFailureException ex) {
        ErrorDto errorDto = new ErrorDto(List.of(new MyError("CONCURRENT_MODIFY",
                null,
                "Please try again later")));
        return errorDto;
    }

}
