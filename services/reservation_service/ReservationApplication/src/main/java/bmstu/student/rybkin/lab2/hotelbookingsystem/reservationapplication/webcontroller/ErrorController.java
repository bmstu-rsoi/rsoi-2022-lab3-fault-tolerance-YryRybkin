package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.webcontroller;


import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models.ErrorResponse;
import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse badRequest(MethodArgumentNotValidException exception) {

        Map<String, String> errors = populateErrors(exception.getBindingResult().getFieldErrors());
        return new ValidationErrorResponse("Validation failed", errors);

    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(CancellationException.class)
    public ErrorResponse transactionCancellation(EntityNotFoundException exception) {

        return new ErrorResponse(exception.getMessage());

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse illegalState(IllegalStateException exception) {

        return new ErrorResponse(exception.getMessage());

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse entityNotFound(EntityNotFoundException exception) {

        return new ErrorResponse(exception.getMessage());

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse runtimeError(RuntimeException exception) {

        return new ErrorResponse(exception.getMessage());

    }

    private Map<String, String> populateErrors(List<FieldError> errors) {

        return errors.stream().collect(Collectors.toMap( FieldError::getField,
                temp -> String.format("Wrong value %s - %s", temp.getRejectedValue(), temp.getDefaultMessage()) ));

    }

}
