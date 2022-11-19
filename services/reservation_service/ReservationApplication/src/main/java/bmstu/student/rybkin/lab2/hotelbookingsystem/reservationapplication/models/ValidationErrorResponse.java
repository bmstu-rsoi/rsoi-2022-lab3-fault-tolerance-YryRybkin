package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import java.util.Map;
import java.util.Objects;

public class ValidationErrorResponse {

    private final String message;
    private final Map<String, String> errors;

    public ValidationErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationErrorResponse that = (ValidationErrorResponse) o;
        return Objects.equals(message, that.message) && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, errors);
    }
}
