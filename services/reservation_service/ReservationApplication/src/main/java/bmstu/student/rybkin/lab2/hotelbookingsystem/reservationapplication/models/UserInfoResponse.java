package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import java.util.List;
import java.util.Objects;

public class UserInfoResponse {

    private final List<ReservationResponse> reservations;
    private final LoyaltyInfoResponse loyalty;

    public UserInfoResponse(List<ReservationResponse> reservations, LoyaltyInfoResponse loyalty) {
        this.reservations = reservations;
        this.loyalty = loyalty;
    }

    public List<ReservationResponse> getReservations() {
        return reservations;
    }

    public LoyaltyInfoResponse getLoyalty() {
        return loyalty;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoResponse that = (UserInfoResponse) o;
        return Objects.equals(reservations, that.reservations) && Objects.equals(loyalty, that.loyalty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservations, loyalty);
    }
}
