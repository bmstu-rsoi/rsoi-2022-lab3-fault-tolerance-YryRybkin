package bmstu.rybkin.lab3.hbs.gatewayapi.webcontroller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@Hidden
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> serviceUnavailable(HttpServerErrorException e) {

        return new ResponseEntity<String>("{ \"message\" : \"" + e.getStatusText() + "\" }", e.getStatusCode());

    }

}
