package bmstu.rybkin.lab3.hbs.loyaltyapplication.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty")
public class Loyalty {

    private @Id @GeneratedValue long id;

    @Column(columnDefinition = "VARCHAR(80) NOT NULL UNIQUE")
    private String username;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer reservationCount;

    @Column(columnDefinition = "VARCHAR(80) NOT NULL DEFAULT 'BRONZE' CHECK (status IN ('BRONZE', 'SILVER', 'GOLD'))")
    private String status;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer discount;

    public Loyalty (Loyalty loyalty) {

        id = loyalty.id;
        username = loyalty.username;
        reservationCount = loyalty.reservationCount;
        status = loyalty.status;
        discount = loyalty.discount;

    }

}
