package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@NoArgsConstructor
@Setter
public class LoyaltyInfoResponse {

    @Pattern(regexp = "^(BRONZE|SILVER|GOLD)$",
                message = "Not a valid status, must be BRONZE, SILVER or GOLD")
    private String status = "BRONZE";
    @Min(0)
    private Integer discount = 5;
    @Min(0)
    private Integer reservationCount = 0;

    public LoyaltyInfoResponse(String status, Integer discount, Integer reservationCount) {
        this.status = status;
        this.discount = discount;
        this.reservationCount = reservationCount;
    }
    public LoyaltyInfoResponse(LoyaltyInfoResponse loyaltyInfoResponse) {

        status = loyaltyInfoResponse.status;
        discount = loyaltyInfoResponse.discount;
        reservationCount = loyaltyInfoResponse.reservationCount;

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
