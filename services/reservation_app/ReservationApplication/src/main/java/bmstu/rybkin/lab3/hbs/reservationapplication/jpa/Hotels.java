package bmstu.rybkin.lab3.hbs.reservationapplication.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotels {

    private @Id @GeneratedValue long id;

    @Column(columnDefinition = "uuid NOT NULL UNIQUE")
    private UUID hotelUid;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "VARCHAR(80) NOT NULL")
    private String country;

    @Column(columnDefinition = "VARCHAR(80) NOT NULL")
    private String city;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String address;

    @Column(columnDefinition = "INT")
    private Integer stars;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer price;

    public Hotels(UUID hotelUid, String name, String country, String city, String address, Integer stars, Integer price) {

        this.hotelUid = hotelUid;
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.stars = stars;
        this.price = price;

    }

    public Hotels(Hotels hotels) {

       id = hotels.id;
       hotelUid = hotels.hotelUid;
       name = hotels.name;;
       country = hotels.country;
       city = hotels.city;
       address = hotels.address;
       stars = hotels.stars;
       price = hotels.price;

    }

}
