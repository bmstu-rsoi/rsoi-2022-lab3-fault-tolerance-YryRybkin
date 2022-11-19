package bmstu.rybkin.lab3.hbs.loyaltyapplication.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class LoyaltyInfoResponse {

    @Pattern(regexp = "^(BRONZE|SILVER|GOLD)$",
                message = "Not a valid status, must be BRONZE, SILVER or GOLD")
    private final String status;
    @Min(0)
    private final Integer discount;
    @Min(0)
    private final Integer reservationCount;

    public LoyaltyInfoResponse(String status, Integer discount, Integer reservationCount) {
        this.status = status;
        this.discount = discount;
        this.reservationCount = reservationCount;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDiscount() {
        return discount;
    }

    public Integer getReservationCount() {
        return reservationCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoyaltyInfoResponse that = (LoyaltyInfoResponse) o;
        return Objects.equals(status, that.status) && Objects.equals(discount, that.discount) && Objects.equals(reservationCount, that.reservationCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, discount, reservationCount);
    }
}
