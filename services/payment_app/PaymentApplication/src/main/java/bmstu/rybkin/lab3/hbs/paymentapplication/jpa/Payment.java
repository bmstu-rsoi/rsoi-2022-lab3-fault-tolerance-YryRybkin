package bmstu.rybkin.lab3.hbs.paymentapplication.jpa;

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
@Table(name = "payment")
public class Payment {

    private @Id @GeneratedValue long id;

    @Column(columnDefinition = "uuid NOT NULL")
    private UUID paymentUid;

    @Column(columnDefinition = "VARCHAR(20) NOT NULL CHECK (status IN('PAID', 'CANCELED'))")
    private String status;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer price;

    public Payment(UUID paymentUid, String status, Integer price) {

        this.paymentUid = paymentUid;
        this.status = status;
        this.price = price;

    }

    public Payment(Payment payment) {

        id = payment.id;
        paymentUid = payment.paymentUid;
        status = payment.status;
        price = payment.price;

    }

}
