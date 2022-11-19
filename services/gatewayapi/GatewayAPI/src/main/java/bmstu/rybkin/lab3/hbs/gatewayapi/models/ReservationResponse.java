package bmstu.rybkin.lab3.hbs.gatewayapi.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Setter
public class ReservationResponse {

    private UUID reservationUid;
    private HotelInfo hotel;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @Pattern(regexp = "^(PAID|RESERVED|CANCELED)$",
                message = "Not a valid status, must be PAID, RESERVED or CANCELED")
    private String status;
    private PaymentInfo payment;

    public ReservationResponse(UUID reservationUid, HotelInfo hotel, Date startDate, Date endDate, String status, PaymentInfo payment) {
        this.reservationUid = reservationUid;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.payment = payment;
    }

    public UUID getReservationUid() {
        return reservationUid;
    }

    public HotelInfo getHotel() {
        return hotel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
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
        ReservationResponse that = (ReservationResponse) o;
        return Objects.equals(reservationUid, that.reservationUid) && Objects.equals(hotel, that.hotel) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(status, that.status) && Objects.equals(payment, that.payment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationUid, hotel, startDate, endDate, status, payment);
    }

}
