package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import java.util.Objects;

public class ErrorDescription {

    private final String field;
    private final String error;

    public ErrorDescription(String field, String error) {
        this.field = field;
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorDescription that = (ErrorDescription) o;
        return Objects.equals(field, that.field) && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, error);
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }
}
