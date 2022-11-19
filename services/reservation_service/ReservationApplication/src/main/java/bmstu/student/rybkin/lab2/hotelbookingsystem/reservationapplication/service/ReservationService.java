package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.service;

import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models.*;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    PaginationResponse getHotels(Integer page, Integer size);
//    UserInfoResponse getMe(String username);
    List<ReservationResponse> getReservations(String username);
    ReservationResponse getReservation(UUID reservationUid, String username);
    CreateReservationResponse postReservation(String username, CreateReservationRequest createReservationRequest);
    void cancelReservation(UUID reservationUid, String username);

}
