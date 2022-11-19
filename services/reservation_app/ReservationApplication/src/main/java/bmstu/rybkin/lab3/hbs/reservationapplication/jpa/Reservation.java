package bmstu.rybkin.lab3.hbs.reservationapplication.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

    private @Id @GeneratedValue long id;

    @Column(columnDefinition = "uuid UNIQUE NOT NULL")
    private UUID reservationUid;

    @Column(columnDefinition = "VARCHAR(80) NOT NULL")
    private String username;

    @Column(columnDefinition = "uuid NOT NULL")
    private UUID paymentUid;

    @Column(columnDefinition = "INT REFERENCES hotels (id)")
    private long hotelId;

    @Column(columnDefinition = "VARCHAR(20) NOT NULL CHECK (status IN ('PAID', 'CANCELED'))")
    private String status;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date startDate;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date endDate;

    public Reservation(UUID reservationUid, String username, UUID paymentUid, long hotelId, String status, Date startDate, Date endDate) {

        this.reservationUid = reservationUid;
        this.username = username;
        this.paymentUid = paymentUid;
        this.hotelId = hotelId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public Reservation(Reservation reservation) {

        id = reservation.id;
        reservationUid = reservation.reservationUid;
        username = reservation.username;
        paymentUid = reservation.paymentUid;
        hotelId = reservation.hotelId;
        status = reservation.status;
        startDate = reservation.startDate;
        endDate = reservation.endDate;

    }

}
