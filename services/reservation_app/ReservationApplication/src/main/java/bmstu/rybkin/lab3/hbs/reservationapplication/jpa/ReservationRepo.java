package bmstu.rybkin.lab3.hbs.reservationapplication.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationUid(UUID reservationUid);
    List<Reservation> findAllByUsername(String username);
    Optional<Reservation> findByReservationUidAndUsername(UUID reservationUid, String username);

    void deleteReservationByReservationUidAndUsername(UUID reservationUid, String username);

}
