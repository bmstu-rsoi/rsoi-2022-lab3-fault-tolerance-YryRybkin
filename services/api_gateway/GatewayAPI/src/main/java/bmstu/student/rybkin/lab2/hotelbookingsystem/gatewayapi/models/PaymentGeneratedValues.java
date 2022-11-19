package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Setter
public class PaymentGeneratedValues {

    private UUID paymentUid;
    private Integer price;

    public PaymentGeneratedValues(UUID paymentUid, Integer price) {
        this.paymentUid = paymentUid;
        this.price = price;
    }

    public PaymentGeneratedValues(PaymentGeneratedValues paymentGeneratedValues) {

        paymentUid = paymentGeneratedValues.paymentUid;
        price = paymentGeneratedValues.price;

    }

    public UUID getPaymentUid() {
        return paymentUid;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentGeneratedValues that = (PaymentGeneratedValues) o;
        return Objects.equals(paymentUid, that.paymentUid) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentUid, price);
    }
}
