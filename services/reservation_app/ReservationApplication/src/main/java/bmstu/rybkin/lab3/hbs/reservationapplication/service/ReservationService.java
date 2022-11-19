package bmstu.rybkin.lab3.hbs.reservationapplication.service;

import bmstu.rybkin.lab3.hbs.reservationapplication.models.CrResServResponse;
import bmstu.rybkin.lab3.hbs.reservationapplication.models.CreateReservationRequest;
import bmstu.rybkin.lab3.hbs.reservationapplication.models.PaginationResponse;
import bmstu.rybkin.lab3.hbs.reservationapplication.models.ResServResponse;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    PaginationResponse getHotels(Integer page, Integer size);
//    UserInfoResponse getMe(String username);
    List<ResServResponse> getReservations(String username);
    ResServResponse getReservation(UUID reservationUid, String username);
    CrResServResponse postReservation(String username, CreateReservationRequest createReservationRequest);
    void cancelReservation(UUID reservationUid, String username);

    void rollbackCancellation(UUID reservationUid, String username);
    void deleteReservation(UUID reservationUid, String username);

}
