package bmstu.rybkin.lab3.hbs.paymentapplication.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

        Optional<Payment> findByPaymentUid(UUID paymentUid);

        void deleteByPaymentUid(UUID paymentUid);

}
