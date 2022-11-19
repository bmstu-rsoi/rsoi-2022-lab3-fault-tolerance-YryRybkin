package bmstu.rybkin.lab3.hbs.reservationapplication.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CrResServResponse {

    private final UUID reservationUid;
    private final UUID hotelUid;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date startDate;
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date endDate;
    @Pattern(regexp = "^(PAID|RESERVED|CANCELED)$",
            message = "Not a valid status, must be PAID, RESERVED or CANCELED")
    private final String status;

    private final UUID paymentUid;

    private final Integer price;

    public CrResServResponse(UUID reservationUid, UUID hotelUid, Date startDate, Date endDate, String status, UUID paymentUid, Integer price) {
        this.reservationUid = reservationUid;
        this.hotelUid = hotelUid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.paymentUid = paymentUid;
        this.price = price;
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

    public String getStatus() {
        return status;
    }

    public UUID getPaymentUid() {
        return paymentUid;
    }

    public Integer getPrice() { return price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrResServResponse that = (CrResServResponse) o;
        return Objects.equals(reservationUid, that.reservationUid) && Objects.equals(hotelUid, that.hotelUid) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate)  && Objects.equals(status, that.status) && Objects.equals(paymentUid, that.paymentUid) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationUid, hotelUid, startDate, endDate, status, paymentUid, price);
    }

}

