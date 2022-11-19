package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.service;

import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.jpa.Hotels;
import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.jpa.HotelsRepo;
import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.jpa.Reservation;
import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.jpa.ReservationRepo;
import bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final HotelsRepo hotelsRepo;
    private final ReservationRepo reservationRepo;


    private static final String GATEWAYURL = "http://gateway:8080/api/v1";
    private static final String X_USER_NAME = "X-User-Name";

    public ReservationServiceImpl(HotelsRepo hotelsRepo, ReservationRepo reservationRepo) {

        this.hotelsRepo = hotelsRepo;
        this.reservationRepo = reservationRepo;

    }

    @Transactional(readOnly = true)
    @Override
    public PaginationResponse getHotels(Integer page, Integer size) {

        List<HotelResponse> hotels = hotelsRepo.findAll().stream().map(
                this::buildHotelResponse).toList();
        int skip = size*(page - 1);
        if (skip > hotels.size())
            throw new NotFoundException(String.format("Page number: %d not found", page));
        return buildPaginationResponse(page, size, hotels.size(),
                hotels.subList(skip, Math.min((skip + size), hotels.size())));

    }

/*
    @Transactional(readOnly = true)
    @Override
    public UserInfoResponse getMe(String username) {

        List<ReservationResponse> resps = getReservations(username);

        String resourceUrl = GATEWAYURL + "/loyalty";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> loyaltyResponse =  noBodyRestCall(resourceUrl, headers, HttpMethod.GET);

        LoyaltyInfoResponse loyaltyInfoResponse;
        if (loyaltyResponse.getStatusCode() != HttpStatus.OK)
            loyaltyInfoResponse = null;
        else
        {

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                loyaltyInfoResponse = objectMapper.readValue(loyaltyResponse.getBody(), LoyaltyInfoResponse.class);
            } catch (Exception e) {
                loyaltyInfoResponse = null;
            }

        }

        return buildUserInfoResponse(resps, loyaltyInfoResponse);

    }
*/

    @Transactional(readOnly = true)
    @Override
    public List<ReservationResponse> getReservations(String username) {

        List<Reservation> reservations = reservationRepo.findAllByUsername(username);
        List<ReservationResponse> resps = new ArrayList<>();
        for (Reservation reservation : reservations)
        {

            Hotels hotel = hotelsRepo.findById(reservation.getHotelId()).orElse(null);
            HotelInfo hotelInfo;
            if (hotel != null)
            {

                String fullAddress = String.join(", ", hotel.getCountry(),
                        hotel.getCity(), hotel.getAddress());
                hotelInfo = buildHotelInfo(hotel.getHotelUid(), hotel.getName(),
                        fullAddress, hotel.getStars());

            }
            else {

                hotelInfo = null;

            }

            PaymentInfo paymentInfo = getPaymentInfo(reservation.getPaymentUid());

            resps.add(buildReservationResponse(reservation.getReservationUid(), hotelInfo,
                    reservation.getStartDate(), reservation.getEndDate(), reservation.getStatus(),
                    paymentInfo));

        }
        return resps;

    }


    @Transactional(readOnly = true)
    @Override
    public ReservationResponse getReservation(UUID reservationUid, String username) {

        Reservation reservation = reservationRepo.findByReservationUidAndUsername(
                reservationUid, username).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Reservation with uid: %s not found for user: %s",
                                reservationUid.toString(), username)) );
        Hotels hotel = hotelsRepo.findById(reservation.getHotelId()).orElseThrow(
                () -> new EntityNotFoundException("Could not find reserved hotel") );
        HotelInfo hotelInfo;
        String fullAddress = String.join(", ", hotel.getCountry(),
                hotel.getCity(), hotel.getAddress());
        hotelInfo = buildHotelInfo(hotel.getHotelUid(), hotel.getName(),
                fullAddress, hotel.getStars());

        PaymentInfo paymentInfo = getPaymentInfo(reservation.getPaymentUid());

        return buildReservationResponse(reservation.getReservationUid(), hotelInfo, reservation.getStartDate(),
                reservation.getEndDate(), reservation.getStatus(), paymentInfo);

    }


    @Transactional
    @Override
    public CreateReservationResponse postReservation(String username,
                                                     CreateReservationRequest createReservationRequest) {

        Hotels hotel = hotelsRepo.findByHotelUid(createReservationRequest.getHotelUid()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Could not find hotel with uid: %s",
                        createReservationRequest.getHotelUid())));

        long diff = ChronoUnit.DAYS.between(createReservationRequest.getStartDate().toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate(),
                createReservationRequest.getEndDate().toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate());
        int price = Math.toIntExact(diff) * hotel.getPrice();

        String resourceUrl = GATEWAYURL + "/loyalty";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        ResponseEntity<String> loyaltyResponse = noBodyRestCall(resourceUrl, headers, HttpMethod.GET);

        if (loyaltyResponse.getStatusCode() != HttpStatus.OK)
            throw new EntityNotFoundException(String.format("Could not find loyalty for user: %s", username));

        ObjectMapper objectMapper = new ObjectMapper();
        LoyaltyInfoResponse loyaltyInfoResponse;
        try {
            loyaltyInfoResponse = objectMapper.readValue(loyaltyResponse.getBody(), LoyaltyInfoResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        price = price * (100 - loyaltyInfoResponse.getDiscount()) / 100;

        UUID paymentUid = UUID.randomUUID();

        ResponseEntity<String> posted = postPayment(paymentUid, price);

        if (posted.getStatusCode() != HttpStatus.CREATED)
            throw new CancellationException(String.format(
                    "Could not create payment with uid: %s", paymentUid));

        postLoyalty(username);

        Reservation reservation = new Reservation(UUID.randomUUID(), username, paymentUid, hotel.getId(),
                "PAID", createReservationRequest.getStartDate(), createReservationRequest.getEndDate());
        reservationRepo.save(reservation);

        PaymentInfo paymentInfo = getPaymentInfo(paymentUid);

        return buildCreateReservationResponse(reservation.getReservationUid(), hotel.getHotelUid(), reservation.getStartDate(),
                reservation.getEndDate(), loyaltyInfoResponse.getDiscount(), reservation.getStatus(), paymentInfo);

    }

    @Transactional
    @Override
    public void cancelReservation(UUID reservationUid, String username) {

        Reservation reservation = reservationRepo.findByReservationUidAndUsername(
                reservationUid, username).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Reservation with uid: %s not found for user: %s",
                                reservationUid.toString(), username)) );

        String url = GATEWAYURL + "/payment" +
                String.format("/%s", reservation.getPaymentUid().toString());
        HttpHeaders paymentHeaders = new HttpHeaders();
        ResponseEntity<String> deletePayment = noBodyRestCall(url, paymentHeaders, HttpMethod.DELETE);

        if (deletePayment.getStatusCode() != HttpStatus.NO_CONTENT)
            throw new CancellationException(String.format(
                    "Could not cancel payment with uid: %s", reservation.getPaymentUid().toString()) );

        url = GATEWAYURL + "/loyalty";
        HttpHeaders loyaltyHeaders = new HttpHeaders();
        loyaltyHeaders.set(X_USER_NAME, username);
        noBodyRestCall(url, loyaltyHeaders, HttpMethod.DELETE);

        reservation.setStatus("CANCELED");
        reservationRepo.save(reservation);

    }

    private ResponseEntity<String> noBodyRestCall(String url, HttpHeaders headers, HttpMethod httpMethod) {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> request = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url,
                httpMethod,
                request,
                String.class
        );

    }

    private ResponseEntity<String> postPayment(UUID paymentUid, Integer price) {

        String resourceUrl = GATEWAYURL + "/payment" +
                String.format("?paymentUid=%s&price=%d", paymentUid.toString(), price);
        HttpHeaders headers = new HttpHeaders();
        return noBodyRestCall(resourceUrl, headers, HttpMethod.POST);

    }

    private ResponseEntity<String> postLoyalty(String username) {

        String resourceUrl = GATEWAYURL + "/loyalty";
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_USER_NAME, username);
        return noBodyRestCall(resourceUrl, headers, HttpMethod.POST);

    }

    private PaymentInfo getPaymentInfo(UUID paymenUid) {

        String resourceUrl = GATEWAYURL + "/payment" +
                String.format("/%s", paymenUid.toString());
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> response = noBodyRestCall(resourceUrl, headers, HttpMethod.GET);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new EntityNotFoundException(String.format("Could not find payment with UUID: %s", paymenUid));

        ObjectMapper objectMapper = new ObjectMapper();
        PaymentInfo paymentInfo;
        try {
            paymentInfo = objectMapper.readValue(response.getBody(), PaymentInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return paymentInfo;

    }
    private HotelResponse buildHotelResponse(Hotels hotel) {

        return new HotelResponse(hotel.getHotelUid(), hotel.getName(), hotel.getCountry(),
                    hotel.getCity(), hotel.getAddress(), hotel.getStars(), hotel.getPrice());

    }

    private PaginationResponse buildPaginationResponse(Integer page, Integer size, Integer total, List<HotelResponse> hotels) {

        return new PaginationResponse(page, size, total, hotels);

    }

    private HotelInfo buildHotelInfo(UUID hotelUid, String name, String fullAddress, Integer stars) {

        return new HotelInfo(hotelUid, name, fullAddress, stars);

    }

    private ReservationResponse buildReservationResponse(UUID reservationUid, HotelInfo hotelInfo,
                                                         Date startDate, Date endDate, String status,
                                                         PaymentInfo paymentInfo) {

        return new ReservationResponse(reservationUid, hotelInfo, startDate, endDate, status, paymentInfo);

    }

    private UserInfoResponse buildUserInfoResponse(List<ReservationResponse> reservationResponse,
                                                   LoyaltyInfoResponse loyaltyInfoResponse) {

        return new UserInfoResponse(reservationResponse, loyaltyInfoResponse);

    }

    private CreateReservationResponse buildCreateReservationResponse(UUID reservationUid, UUID hotelUid,
                                                                     Date startDate, Date endDate,
                                                                     Integer discount, String status,
                                                                     PaymentInfo paymentInfo) {

        return  new CreateReservationResponse(reservationUid, hotelUid, startDate, endDate, discount, status, paymentInfo);

    }

}
