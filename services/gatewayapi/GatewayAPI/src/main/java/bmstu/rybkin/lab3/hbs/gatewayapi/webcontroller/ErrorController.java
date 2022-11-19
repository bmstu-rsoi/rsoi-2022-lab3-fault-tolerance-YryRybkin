package bmstu.rybkin.lab3.hbs.gatewayapi.webcontroller;

import bmstu.rybkin.lab3.hbs.gatewayapi.models.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.NoRouteToHostException;

@Hidden
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> serviceUnavailable(HttpServerErrorException e) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>("{ \"message\" : \"" + e.getStatusText() + "\" }", headers, e.getStatusCode());


    }

    @ExceptionHandler(ConnectException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse serviceUnavailable(ConnectException e) {

        return new ErrorResponse(e.getMessage());

    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse serviceUnavailable_2(ConnectException e) {

        return new ErrorResponse(e.getMessage());

    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse runtimeException(RuntimeException e) {

        return new ErrorResponse(e.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(Exception e) {

        return new ErrorResponse(e.getMessage());

    }

    @ExceptionHandler(NoRouteToHostException.class)
    public ErrorResponse exception(NoRouteToHostException e) {

        return new ErrorResponse(e.getMessage());

    }

}
