package bmstu.rybkin.lab3.hbs.gatewayapi.models;

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
    private String status = null;
    @Min(0)
    private Integer discount = null;
    @Min(0)
    private Integer reservationCount = null;

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
