package bmstu.rybkin.lab3.hbs.gatewayapi.models;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Setter
public class HotelInfo {

    private UUID hotelUid;
    private String name;
    private String fullAddress;
    private Integer stars;

    public HotelInfo(UUID hotelUid, String name, String fullAddress, Integer stars) {
        this.hotelUid = hotelUid;
        this.name = name;
        this.fullAddress = fullAddress;
        this.stars = stars;
    }

    public UUID getHotelUid() {
        return hotelUid;
    }

    public String getName() {
        return name;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public Integer getStars() {
        return stars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelInfo hotelInfo = (HotelInfo) o;
        return Objects.equals(hotelUid, hotelInfo.hotelUid) && Objects.equals(name, hotelInfo.name) && Objects.equals(fullAddress, hotelInfo.fullAddress) && Objects.equals(stars, hotelInfo.stars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelUid, name, fullAddress, stars);
    }

}
