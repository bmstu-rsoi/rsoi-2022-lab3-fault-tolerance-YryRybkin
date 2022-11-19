package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CreateReservationResponse {

    private final UUID reservationUid;
    private final UUID hotelUid;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date startDate;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date endDate;
    private final Integer discount;
    @Pattern(regexp = "^(PAID|RESERVED|CANCELED)$",
                message = "Not a valid status, must be PAID, RESERVED or CANCELED")
    private final String status;
    private final PaymentInfo payment;

    public CreateReservationResponse(UUID reservationUid, UUID hotelUid, Date startDate, Date endDate, Integer discount, String status, PaymentInfo payment) {
        this.reservationUid = reservationUid;
        this.hotelUid = hotelUid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discount = discount;
        this.status = status;
        this.payment = payment;
    }

    public UUID getReservationUid() {
        return reservationUid;
    }

    public UUID getHotelUid() {
        return hotelUid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getDiscount() {
        return discount;
    }

    public String getStatus() {
        return status;
    }

    public PaymentInfo getPayment() {
        return payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateReservationResponse that = (CreateReservationResponse) o;
        return Objects.equals(reservationUid, that.reservationUid) && Objects.equals(hotelUid, that.hotelUid) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(discount, that.discount) && Objects.equals(status, that.status) && Objects.equals(payment, that.payment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationUid, hotelUid, startDate, endDate, discount, status, payment);
    }

}
