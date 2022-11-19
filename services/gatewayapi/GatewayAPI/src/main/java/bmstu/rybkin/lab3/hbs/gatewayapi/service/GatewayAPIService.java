package bmstu.rybkin.lab3.hbs.gatewayapi.service;

import bmstu.rybkin.lab3.hbs.gatewayapi.models.CreateReservationRequest;
import bmstu.rybkin.lab3.hbs.gatewayapi.models.CreateReservationResponse;
import bmstu.rybkin.lab3.hbs.gatewayapi.models.ReservationResponse;
import bmstu.rybkin.lab3.hbs.gatewayapi.models.UserInfoResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface GatewayAPIService {

    ResponseEntity<String> getLoyaltyInfo(String username);
    ResponseEntity<String> incrementLoyalty(String username);
    ResponseEntity<String> decrementLoyalty(String username);
    ResponseEntity<String> postPayment(UUID paymentUid, Integer price);
    ResponseEntity<String> getPayment(UUID paymentUid);
    public ResponseEntity<String> cancelPayment(UUID paymentUid);
    ResponseEntity<String> getHotels(Integer page, Integer size);
    List<ReservationResponse> getReservations(String username);
    ReservationResponse getReservation(UUID reservationUid, String username);
    CreateReservationResponse postReservation(String username,
                                              CreateReservationRequest createReservationRequest);
    void cancelReservation(UUID reservationUid, String username);
    UserInfoResponse getMe(String username);

}
