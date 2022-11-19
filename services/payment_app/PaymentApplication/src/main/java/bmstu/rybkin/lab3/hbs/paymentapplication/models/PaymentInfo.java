package bmstu.rybkin.lab3.hbs.paymentapplication.models;

import javax.validation.constraints.Pattern;
import java.util.Objects;

public class PaymentInfo {

    @Pattern(regexp = "^(PAID|RESERVED|CANCELED)$", message = "Not a valid status, must be PAID, RESERVED or CANCELED")
    private final String status;
    private final Integer price;

    public PaymentInfo(String status, Integer price) {
        this.status = status;
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentInfo that = (PaymentInfo) o;
        return Objects.equals(status, that.status) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, price);
    }
}
