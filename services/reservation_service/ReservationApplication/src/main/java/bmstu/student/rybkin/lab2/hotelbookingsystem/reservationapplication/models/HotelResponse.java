package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import java.util.Objects;
import java.util.UUID;

public class HotelResponse {

    private final UUID hotelUid;
    private final String name;
    private final String country;
    private final String city;
    private final String address;
    private final Integer stars;
    private final Integer price;

    public HotelResponse(UUID hotelUid, String name, String country, String city, String address, Integer stars, Integer price) {
        this.hotelUid = hotelUid;
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.stars = stars;
        this.price = price;
    }

    public UUID getHotelUid() {
        return hotelUid;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public Integer getStars() {
        return stars;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelResponse that = (HotelResponse) o;
        return Objects.equals(hotelUid, that.hotelUid) && Objects.equals(name, that.name) && Objects.equals(country, that.country) && Objects.equals(city, that.city) && Objects.equals(address, that.address) && Objects.equals(stars, that.stars) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelUid, name, country, city, address, stars, price);
    }

}
