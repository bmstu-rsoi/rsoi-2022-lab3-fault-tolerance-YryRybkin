package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CreateReservationRequest {

    private final UUID hotelUid;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final Date startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final Date endDate;

    public CreateReservationRequest(UUID hotelUid, Date startDate, Date endDate) {
        this.hotelUid = hotelUid;
        this.startDate = startDate;
        this.endDate = endDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateReservationRequest that = (CreateReservationRequest) o;
        return Objects.equals(hotelUid, that.hotelUid) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelUid, startDate, endDate);
    }

}
